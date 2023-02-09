package cn.holmes.code;

import cn.holmes.code.qr.ImgQrTool;

public class Main {

    public static void main(String[] args) {
        ImgQrTool.createSimpleQr("https://holmes.cn", 500, 500, "simple-code.jpg");
        ImgQrTool.createImageQr("https://holmes.cn", 500, 500, "core.png", "image-code.jpg");
    }
}
