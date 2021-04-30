package com.lzk.chatroom.controller;

import com.alibaba.fastjson.JSONObject;
import com.lzk.chatroom.utils.ResponseEntityUtil;
import com.lzk.chatroom.utils.TimeHelper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.math.BigDecimal;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/FmsService")
@Slf4j
public class FmsController {

    @Value("${fmsUpload}")
    private String UPLOAD_PATH;

    /**
     * 单文件上传
     *
     * @param request
     * @return
     */

    @RequestMapping(value = "/upload", method = RequestMethod.POST)
    public ResponseEntity add(HttpServletRequest request) throws IOException {
        String name = request.getParameter("name");
        MultipartHttpServletRequest fileReq = (MultipartHttpServletRequest) request;
        Iterator<String> iter = fileReq.getFileNames();
        JSONObject obj = new JSONObject();
        //此处没有用while的原因，是因为LayUI框架的多文件上传，其实是逐个调用接口上传
        if (iter.hasNext()) {
            MultipartFile reqFile = fileReq.getFile(iter.next());
            String suffix = reqFile.getOriginalFilename()
                    .substring(reqFile.getOriginalFilename().lastIndexOf('.') + 1);

            String fileName = UUID.randomUUID().toString()
                    .replace("-", "") + "_" + reqFile.getOriginalFilename().substring(0, reqFile.getOriginalFilename().lastIndexOf('.')) + "." + suffix;


            String path = UPLOAD_PATH + "/" + TimeHelper.getDateYYYY() + "/"
                    + TimeHelper.getDateMM() + "/" + TimeHelper.getDateDay();

            File filePath = new File(path);
            if (!filePath.exists()) {
                filePath.mkdirs();
            }
            String fileUrl = path + "/" + fileName;
            File file = new File(fileUrl);
            reqFile.transferTo(file);

            if (StringUtils.isNotBlank(name)) {
                obj.put("name", name);
            } else {
                obj.put("name", reqFile.getOriginalFilename());
            }
            obj.put("path", fileUrl.replace(UPLOAD_PATH, ""));
            obj.put("size", getFileSize(file.length()));
        }
        return ResponseEntityUtil.success(obj);
    }


    public String getFileSize(long length) {
        BigDecimal a = new BigDecimal(length);
        BigDecimal b = new BigDecimal(1024);

        BigDecimal d1 = a.divide(b, 1, BigDecimal.ROUND_HALF_UP);//KB
        if (d1.doubleValue() > 1000) {
            BigDecimal d2 = d1.divide(b, 1, BigDecimal.ROUND_HALF_UP);//MB
            return d2.toString() + "MB";
        }
        return d1.toString() + "KB";

    }


    /**
     * 文件下载接口
     *
     * @param filePath 文件上传时，返回的相对路径
     * @param response
     * @param isOnLine 传入true，表示打开，但是打开的是浏览器能识别的文件，比如图片、pdf，word等无法打开
     *                 传入false,只是下载，如果不传入这个参数默认为false
     * @throws Exception
     */
    @RequestMapping(value = "/downloadFile", method = RequestMethod.GET)
    public void downLoad(String filePath, HttpServletResponse response, boolean isOnLine, String fName) throws Exception {
//		String realPath = "D:/File";
        String realPath = UPLOAD_PATH + "/";
        File f = new File(realPath + filePath);
        if (!f.exists()) {
            response.sendError(404, "File not found!");
            return;
        }
        String fileName = f.getName().substring(f.getName().indexOf("_") + 1);
//        fileName = new String(fileName.getBytes("UTF-8"), "ISO-8859-1");
        if (StringUtils.isNotBlank(fName)) fileName = fName;
        BufferedInputStream br = new BufferedInputStream(new FileInputStream(f));
        byte[] buf = new byte[1024];
        int len = 0;
        response.reset(); // 非常重要
        if (isOnLine) { // 在线打开方式
            URL u = new URL("file:///" + realPath + filePath);
            response.setContentType(u.openConnection().getContentType());
            response.setHeader("Content-Disposition", "inline; filename=" + URLEncoder.encode(fileName, "UTF-8"));
            // 文件名应该编码成UTF-8
        } else { // 纯下载方式
            response.setContentType("application/x-msdownload");
            response.setHeader("Content-Disposition", "attachment; filename=" + URLEncoder.encode(fileName, "UTF-8"));
        }
        OutputStream out = response.getOutputStream();
        while ((len = br.read(buf)) > 0)
            out.write(buf, 0, len);
        br.close();
        out.close();
    }

    /**
     * @param num 0用户导入模板  1资产导入模板
     */
    @GetMapping("/downLoadTemplate")
    public void downLoadTemplate(Integer num, HttpServletResponse response) {
        try {
            if (num == 0) {
                String filePath = "ExcelTemplate/用户批量导入模板.xlsx";
                downLoad(filePath, response, false, null);
            }
            if (num == 1) {
                String filePath = "ExcelTemplate/资产批量导入模板.xlsx";
                downLoad(filePath, response, false, null);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @RequestMapping(value = "/deleteFile", method = RequestMethod.POST)
    public ResponseEntity deleteFile(@RequestBody List<String> filePaths) {
        try {
            if (filePaths != null && filePaths.size() > 0) {
                System.gc();
                for (int i = 0; i < filePaths.size(); i++) {
                    File file = new File(UPLOAD_PATH, filePaths.get(i));
                    if (file.exists()) {
                        file.delete();
                    } else {
                        log.info("文件不存在：{}", filePaths.get(i));
                    }
                }
                return ResponseEntityUtil.success(200, "文件删除成功");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntityUtil.fail();
        }
        return ResponseEntityUtil.success();
    }

    public ResponseEntity limitSize(Integer sizeMB, HttpServletRequest request) {
        try {
            MultipartHttpServletRequest fileReq = (MultipartHttpServletRequest) request;
            Iterator<String> fileNames = fileReq.getFileNames();
            if (fileNames.hasNext()) {
                MultipartFile multipartFile = fileReq.getFile(fileNames.next());
                String fileSize = getFileSize(multipartFile.getSize());
                if (fileSize.contains("MB")) {
                    String substring = StringUtils.substring(fileSize, 0, fileSize.length() - 2);
                    if (Double.parseDouble(substring) > sizeMB) {
                        return ResponseEntityUtil.success(200, "文件大小超过" + sizeMB + "MB");
                    }
                }
                return add(request);
            }
            return ResponseEntityUtil.success();
        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntityUtil.fail();
        }
    }


}
