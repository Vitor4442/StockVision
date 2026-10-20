package stockvision.inventory.product;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.oned.Code128Writer;
import com.google.zxing.qrcode.QRCodeWriter;
import org.springframework.stereotype.Service;

import java.awt.image.BufferedImage;

@Service
public class BarcodeService {

    public BufferedImage generateCode(String trackingCode) throws WriterException {
        var matrix = new Code128Writer().encode(
                trackingCode,
                BarcodeFormat.CODE_128,
                400,
                120
        );

        return MatrixToImageWriter.toBufferedImage(matrix);
    }

    public BufferedImage generateQrCode(String trackingCode) throws WriterException {
        var matrix = new QRCodeWriter().encode(
                trackingCode,
                BarcodeFormat.QR_CODE,
                400,
                120
        );

        return MatrixToImageWriter.toBufferedImage(matrix);
    }


}