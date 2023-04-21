package com.shmet.api;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Map;
import java.util.UUID;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.support.StandardMultipartHttpServletRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.shmet.IOUtils;
import com.shmet.bean.PostResponseBean;
import com.shmet.bean.ScriptBean;
import com.shmet.dao.mongodb.ScriptConfigDao;
import com.shmet.entity.mongo.ScriptConfig;
import com.shmet.exception.EmsException;
import com.shmet.helper.crypt.AESCrypt;

/**
 * 文件上传下载的相关API
 */
@CrossOrigin
@Controller
public class CameraController {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Value("${filepath.camera_image_meter.uploadbasepath}")
    String uploadBasePath;

    @Value("${filepath.script.basepath}")
    String scriptBasePath;

    @Value("${filepath.script.temppath}")
    String tempBasePath;

    @Autowired
    ScriptConfigDao scriptConfigDao;

    String sKey = "juZhen|/?}[43BNV";
    String ivParameter = "juZheN!@#$%^65()";

    private final AESCrypt aes = new AESCrypt(sKey, ivParameter);

    @ResponseBody
    @RequestMapping(value = "/camera/image/meter/upload", method = RequestMethod.POST)
    public PostResponseBean upload(@RequestParam("edata") String edata, HttpServletRequest request,
                                   HttpServletResponse response) {
        PostResponseBean rst = new PostResponseBean();
        try {
            String jsonData = aes.decrypt(edata);
            if (jsonData == null) {
                rst.setSuccessed(false);
            } else {
                ObjectMapper om = new ObjectMapper();
                ScriptBean camImgBean;

                camImgBean = om.readValue(jsonData, ScriptBean.class);
                String path =
                        uploadBasePath + "/" + camImgBean.getSubProjectId() + "_" + camImgBean.getGatewayId();
                File dir = new File(path);
                if (!dir.exists()) {
                    dir.mkdirs();
                }
                StandardMultipartHttpServletRequest req = (StandardMultipartHttpServletRequest) request;
                Map<String, MultipartFile> files = req.getFileMap();
                for (String key : files.keySet()) {
                    MultipartFile file = files.get(key);
                    FileOutputStream fileOutput = null;
                    try {
                        fileOutput = new FileOutputStream(path + "/" + file.getOriginalFilename());
                        fileOutput.write(file.getBytes());
                        fileOutput.flush();
                        rst.setSuccessed(true);
                    } catch (Exception e) {
                        throw new EmsException(e);
                    } finally {
                        if (fileOutput != null) {
                            try {
                                fileOutput.close();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
            }
        } catch (Throwable t) {
            rst.setSuccessed(false);
            rst.setErrorMsg(t.getMessage());
            logger.warn("上传图片出现异常!", t);
        }
        return rst;
    }

    @ResponseBody
    @RequestMapping(method = RequestMethod.GET, value = "/script/config")
    public ScriptConfig getScriptConfig(@RequestParam("edata") String edata) {
        ScriptConfig rst = null;
        try {
            String jsonData = aes.decrypt(edata);
            if (jsonData != null) {
                ObjectMapper om = new ObjectMapper();
                ScriptBean scriptBean;
                scriptBean = om.readValue(jsonData, ScriptBean.class);
                rst = scriptConfigDao.findByProjectScriptGateway(scriptBean.getSubProjectId(), scriptBean.getScriptId(), scriptBean.getGatewayId());
            }
        } catch (Throwable t) {
            logger.warn("获取脚本配置信息出现异常！", t);
        }
        return rst;
    }

    @RequestMapping("/script/latest")
    public String downloadFile(@RequestParam("edata") String edata, HttpServletRequest request, HttpServletResponse response) {
        try {
            String jsonData = aes.decrypt(edata);
            if (jsonData != null) {
                ObjectMapper om = new ObjectMapper();
                ScriptBean scriptBean;
                scriptBean = om.readValue(jsonData, ScriptBean.class);
                ScriptConfig scriptConfig = scriptConfigDao.findByProjectScriptGateway(scriptBean.getSubProjectId(), scriptBean.getScriptId(), scriptBean.getGatewayId());
                String scFileDir = scriptBasePath + "/" + scriptConfig.getScriptId() + "_" + scriptConfig.getSubProjectId() + "_" + scriptConfig.getGatewayId() + "/" + scriptConfig.getVer();
                String zipName = UUID.randomUUID().toString() + ".zip";
                File fileZip = new File(tempBasePath + "/" + zipName);
                try (FileOutputStream outStream = new FileOutputStream(fileZip)) {
                    IOUtils.toZip(scFileDir, outStream, true);
                }
                IOUtils.downloadFile(fileZip, response, true);
            }
        } catch (Throwable t) {
            logger.warn("下载文件出现异常！", t);
        }
        return null;
    }

}
