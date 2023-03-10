package cn.holmes.code.qr;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;


/**
 * 内嵌图片的二维码生成器
 *
 * @author linhuid
 * @date 2019/5/16 16:41
 * @since v1.0.0
 */
public class ImgQrTool {

    private static final Logger logger = LoggerFactory.getLogger(ImgQrTool.class);

    /**
     * 镶嵌的图片宽度的一般
     */
    private static final int IMAGE_WIDTH = 80;
    private static final int IMAGE_HEIGHT = 80;
    private static final int IMAGE_HALF_WIDTH = IMAGE_WIDTH / 2;
    private static final int FRAME_WIDTH = 2;

    /**
     * 二维码写码器
     */
    private static final MultiFormatWriter MULTI_WRITER = new MultiFormatWriter();

    /**
     * 生成带图片的二维码
     *
     * @param content       二维码的内容
     * @param width         宽度
     * @param height        高度
     * @param srcImagePath  被镶嵌的图片的地址
     * @param destImagePath 生成二维码图片的地址
     * @since 2.3.0
     */
    public static void createImageQr(String content, int width, int height, String srcImagePath, String destImagePath) {
        try {
            ImageIO.write(genBarcode(content, width, height, srcImagePath), "jpg", new File(destImagePath));
        } catch (IOException | WriterException e) {
            e.printStackTrace();
        }
    }

    /**
     * 创建不带参数的二维码
     *
     * @since 2.3.0
     */
    public static void createSimpleQr(String content, int width, int height, String destImagePath) {
        try (FileOutputStream output = new FileOutputStream(destImagePath)) {
            // 图像类型
            String format = "jpg";
            Map<EncodeHintType, Object> hints = new HashMap<>();
            hints.put(EncodeHintType.CHARACTER_SET, "UTF-8");
            hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.M);
            hints.put(EncodeHintType.MARGIN, 1);
            // 生成矩阵
            BitMatrix bitMatrix = new MultiFormatWriter().encode(content, BarcodeFormat.QR_CODE, width, height, hints);
            // 输出图像
            MatrixToImageWriter.writeToStream(bitMatrix, format, output);
        } catch (Exception e) {
            logger.error("生成二维码出错！ImgQrTool：createSimpleQr()", e);
        }
    }

    private static BufferedImage genBarcode(String content, int width, int height, String srcImagePath) throws WriterException, IOException {
        // 读取源图像
        BufferedImage scaleImage = scale(srcImagePath, IMAGE_WIDTH, IMAGE_HEIGHT, true);
        int[][] srcPixels = new int[IMAGE_WIDTH][IMAGE_HEIGHT];
        for (int i = 0; i < scaleImage.getWidth(); i++) {
            for (int j = 0; j < scaleImage.getHeight(); j++) {
                srcPixels[i][j] = scaleImage.getRGB(i, j);
            }
        }

        Map<EncodeHintType, Object> hint = new HashMap<>();
        hint.put(EncodeHintType.CHARACTER_SET, "utf-8");
        hint.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.H);
        // 二维码整体白框
        hint.put(EncodeHintType.MARGIN, 1);

        // 生成二维码
        BitMatrix matrix = MULTI_WRITER.encode(content, BarcodeFormat.QR_CODE, width, height, hint);

        // 二维矩阵转为一维像素数组
        int halfW = matrix.getWidth() / 2;
        int halfH = matrix.getHeight() / 2;
        int[] pixels = new int[width * height];

        for (int y = 0; y < matrix.getHeight(); y++) {
            for (int x = 0; x < matrix.getWidth(); x++) {
                // 读取图片
                if (x > halfW - IMAGE_HALF_WIDTH && x < halfW + IMAGE_HALF_WIDTH && y > halfH - IMAGE_HALF_WIDTH && y < halfH + IMAGE_HALF_WIDTH) {
                    pixels[y * width + x] = srcPixels[x - halfW + IMAGE_HALF_WIDTH][y - halfH + IMAGE_HALF_WIDTH];
                }
                // 在图片四周形成边框
                else if ((x > halfW - IMAGE_HALF_WIDTH - FRAME_WIDTH && x < halfW - IMAGE_HALF_WIDTH + FRAME_WIDTH && y > halfH - IMAGE_HALF_WIDTH - FRAME_WIDTH && y < halfH + IMAGE_HALF_WIDTH + FRAME_WIDTH)
                        || (x > halfW + IMAGE_HALF_WIDTH - FRAME_WIDTH && x < halfW + IMAGE_HALF_WIDTH + FRAME_WIDTH && y > halfH - IMAGE_HALF_WIDTH - FRAME_WIDTH && y < halfH + IMAGE_HALF_WIDTH + FRAME_WIDTH)
                        || (x > halfW - IMAGE_HALF_WIDTH - FRAME_WIDTH && x < halfW + IMAGE_HALF_WIDTH + FRAME_WIDTH && y > halfH - IMAGE_HALF_WIDTH - FRAME_WIDTH && y < halfH - IMAGE_HALF_WIDTH + FRAME_WIDTH)
                        || (x > halfW - IMAGE_HALF_WIDTH - FRAME_WIDTH && x < halfW + IMAGE_HALF_WIDTH + FRAME_WIDTH && y > halfH + IMAGE_HALF_WIDTH - FRAME_WIDTH && y < halfH + IMAGE_HALF_WIDTH + FRAME_WIDTH)) {
                    pixels[y * width + x] = 0xfffffff;
                } else {
                    // 此处可以修改二维码的颜色，可以分别制定二维码和背景的颜色；
                    pixels[y * width + x] = matrix.get(x, y) ? 0xff000000 : 0xfffffff;
                }
            }
        }

        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        image.getRaster().setDataElements(0, 0, width, height, pixels);

        return image;
    }

    /**
     * 把传入的原始图像按高度和宽度进行缩放，生成符合要求的图标
     *
     * @param srcImageFile 源文件地址
     * @param height       目标高度
     * @param width        目标宽度
     * @param hasFiller    比例不对时是否需要补白：true为补白; false为不补白;
     * @throws IOException
     */
    private static BufferedImage scale(String srcImageFile, int height, int width, boolean hasFiller) throws IOException {
        double ratio; // 缩放比例
        File file = new File(srcImageFile);
        BufferedImage srcImage = ImageIO.read(file);
        Image destImage = srcImage.getScaledInstance(width, height, BufferedImage.SCALE_SMOOTH);
        // 计算比例
        if ((srcImage.getHeight() > height) || (srcImage.getWidth() > width)) {
            if (srcImage.getHeight() > srcImage.getWidth()) {
                ratio = (new Integer(height)).doubleValue() / srcImage.getHeight();
            } else {
                ratio = (new Integer(width)).doubleValue() / srcImage.getWidth();
            }
            AffineTransformOp op = new AffineTransformOp(AffineTransform.getScaleInstance(ratio, ratio), null);
            destImage = op.filter(srcImage, null);
        }
        if (hasFiller) {
            // 补白
            BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
            Graphics2D graphic = image.createGraphics();
            graphic.setColor(Color.white);
            graphic.fillRect(0, 0, width, height);
            if (width == destImage.getWidth(null)) {
                graphic.drawImage(destImage, 0, (height - destImage.getHeight(null)) / 2, destImage.getWidth(null), destImage.getHeight(null), Color.white, null);
            } else {
                graphic.drawImage(destImage, (width - destImage.getWidth(null)) / 2, 0, destImage.getWidth(null), destImage.getHeight(null), Color.white, null);
            }
            graphic.dispose();
            destImage = image;
        }
        return (BufferedImage) destImage;
    }

    /**
     * 生成上方带文字的二维码
     */
    public static void createQrWithFontsAbove(QrImage para) {
        try {
            // 首先生成二维码图片
            BufferedImage qrImage = genBarcode(para.getQrContent(), para.getQrWidth(), para.getQrHeight(), para.getQrIconFilePath());

            int qrImageWidth = qrImage.getWidth();
            int qrImageHeight = qrImage.getHeight();

            String[] splitStrLines = splitStrLines(para.getWordContent(), qrImageWidth / para.getWordSize());
            //防止文字遮挡
            int fontsImageHeight = splitStrLines.length * para.getWordSize() + 10;

            //创建顶部文字的图片
            BufferedImage imageWithFonts = new BufferedImage(qrImageWidth, fontsImageHeight, BufferedImage.TYPE_4BYTE_ABGR);
            Graphics fontsImageGraphics = imageWithFonts.getGraphics();
            fontsImageGraphics.fillRect(0, 0, qrImageWidth, fontsImageHeight);
            fontsImageGraphics.setColor(Color.black);
            fontsImageGraphics.setFont(new Font("宋体", Font.PLAIN, para.getWordSize()));

            //文字长度大于一行的长度，进行分行 每行限制的文字个数
            if (para.getWordContent().length() > qrImageWidth / para.getWordSize()) {
                for (int i = 0; i < splitStrLines.length; i++) {
                    fontsImageGraphics.drawString(splitStrLines[i], 0, para.getWordSize() * (i + 1));
                }
            } else {
                fontsImageGraphics.drawString(
                        para.getWordContent(),
                        //总长度减去字长度除以2为向右偏移长度
                        ((qrImageWidth / para.getWordSize() - para.getWordContent().length()) / 2) * para.getWordSize() + 20,
                        para.getWordSize());
            }

            // 从图片中读取RGB
            int[] imageArrayFonts = new int[qrImageWidth * fontsImageHeight];
            imageArrayFonts = imageWithFonts.getRGB(0, 0, qrImageWidth, fontsImageHeight, imageArrayFonts, 0, qrImageWidth);

            int[] imageArrayQr = new int[qrImageWidth * qrImageHeight];
            imageArrayQr = qrImage.getRGB(0, 0, qrImageWidth, qrImageHeight, imageArrayQr, 0, qrImageWidth);

            // 生成新图片
            BufferedImage imageNew = new BufferedImage(qrImageWidth, qrImageHeight + fontsImageHeight, BufferedImage.TYPE_INT_RGB);
            // 设置上半部分的RGB
            imageNew.setRGB(0, 0, qrImageWidth, fontsImageHeight, imageArrayFonts, 0, qrImageWidth);
            // 设置下半部分的RGB
            imageNew.setRGB(0, fontsImageHeight, qrImageWidth, qrImageHeight, imageArrayQr, 0, qrImageWidth);

            File outFile = new File(para.getFileOutputPath());
            ImageIO.write(imageNew, "jpg", outFile);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 一行字符串拆分成多行
     */
    private static String[] splitStrLines(String str, int len) {
        int blocks = str.length() / len + 1;
        String[] strs = new String[blocks];
        for (int i = 0; i < blocks; i++) {
            if ((i + 1) * len > str.length()) {
                strs[i] = str.substring(i * len);
            } else {
                strs[i] = str.substring(i * len, (i + 1) * len);
            }
        }
        return strs;
    }

}
