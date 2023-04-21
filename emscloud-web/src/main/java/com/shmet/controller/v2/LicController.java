package com.shmet.controller.v2;

import cn.hutool.core.util.StrUtil;
import com.shmet.utils.LicenseUtil;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.security.PrivateKey;
import java.security.PublicKey;

@RestController
@RequestMapping("/license")
@RequiredArgsConstructor
public class LicController {
    @RequestMapping("/gen")
    public void generate(String date, String rjh, String xkz, Long sc, HttpServletResponse response) {
        if (StrUtil.isBlank(date)) {
            throw new RuntimeException("日期不能为空");
        }
        if (StrUtil.isBlank(rjh)) {
            throw new RuntimeException("软件号不能为空");
        }
        if (StrUtil.isBlank(xkz)) {
            throw new RuntimeException("许可证不能为空");
        }
        if (sc == null) {
            throw new RuntimeException("时长不能为空");
        }

        String str = date + "$" + rjh + "$" + xkz + "$" + sc;
        PrivateKey privateKey = LicenseUtil.getPrivateKey("lic.pri", "RSA");
        PublicKey publicKey = LicenseUtil.getPublicKey("lic.pub", "RSA");

        String encrypt = LicenseUtil.encrypt(privateKey, publicKey, str);
        byte[] bytes = encrypt.getBytes();

        try {
            String fileName = System.currentTimeMillis() + ".txt";
            response.setHeader("Content-Disposition", "attachment; filename=" + fileName);
            ServletOutputStream out = response.getOutputStream();
            BufferedOutputStream bufferOut = new BufferedOutputStream(out);
            bufferOut.write(bytes);
            bufferOut.flush();
            bufferOut.close();
            out.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Getter
    @Setter
    static class GenReq {
        //yyyy-MM-dd
        @NotBlank(message = "日期不能为空")
        private String date;
        //软件号
        @NotBlank(message = "软件好不能为空")
        private String rjh;
        //许可证
        @NotBlank(message = "许可证不能为空")
        private String xkz;
        //时长 默认 365*24*60*60
        @NotNull(message = "时长不能为空")
        private Long sc;
    }
}
