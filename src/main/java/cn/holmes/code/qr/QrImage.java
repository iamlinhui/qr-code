package cn.holmes.code.qr;

/**
 * 二维码图片对象
 *
 * @author linhuid
 * @date 2019/5/16 16:50
 * @since v1.0.0
 */
public class QrImage {

    /**
     * 二维码的内容
     */
    private String qrContent;

    /**
     * 二维码的宽度
     */
    private int qrWidth;

    /**
     * 二维码的高度
     */
    private int qrHeight;

    /**
     * 二维码中间图标的文件路径
     */
    private String qrIconFilePath;

    /**
     * 二维码中间小图标的边长
     */
    private int qrIconWidth;

    /**
     * 顶部文字的高度
     */
    private int topWordHeight;

    /**
     * 文字的大小
     */
    private int wordSize;

    /**
     * 文字的内容
     */
    private String wordContent;

    /**
     * 文件的输出路径
     */
    private String fileOutputPath;


    public String getQrContent() {
        return qrContent;
    }

    public void setQrContent(String qrContent) {
        this.qrContent = qrContent;
    }

    public int getQrWidth() {
        return qrWidth;
    }

    public void setQrWidth(int qrWidth) {
        this.qrWidth = qrWidth;
    }

    public int getQrHeight() {
        return qrHeight;
    }

    public void setQrHeight(int qrHeight) {
        this.qrHeight = qrHeight;
    }

    public String getQrIconFilePath() {
        return qrIconFilePath;
    }

    public void setQrIconFilePath(String qrIconFilePath) {
        this.qrIconFilePath = qrIconFilePath;
    }

    public int getQrIconWidth() {
        return qrIconWidth;
    }

    public void setQrIconWidth(int qrIconWidth) {
        this.qrIconWidth = qrIconWidth;
    }

    public int getTopWordHeight() {
        return topWordHeight;
    }

    public void setTopWordHeight(int topWordHeight) {
        this.topWordHeight = topWordHeight;
    }

    public int getWordSize() {
        return wordSize;
    }

    public void setWordSize(int wordSize) {
        this.wordSize = wordSize;
    }

    public String getWordContent() {
        return wordContent;
    }

    public void setWordContent(String wordContent) {
        this.wordContent = wordContent;
    }

    public String getFileOutputPath() {
        return fileOutputPath;
    }

    public void setFileOutputPath(String fileOutputPath) {
        this.fileOutputPath = fileOutputPath;
    }
}
