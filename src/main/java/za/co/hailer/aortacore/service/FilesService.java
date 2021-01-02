package za.co.hailer.aortacore.service;

import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.ImageType;
import org.apache.pdfbox.rendering.PDFRenderer;
import org.apache.tika.Tika;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import za.co.hailer.aortacore.model.UploadResponse;
import za.co.hailer.aortacore.repository.UploadResponseRepository;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Service
public class FilesService {
    private final UploadResponseRepository uploadResponseRepository;
    public FilesService(
            UploadResponseRepository uploadResponseRepository
    ){
        this.uploadResponseRepository = uploadResponseRepository;
    }

    private File convert(MultipartFile multipartFile) throws IOException{
        File convertedFile = new File(multipartFile.getOriginalFilename());
        convertedFile.createNewFile();
        FileOutputStream fileOutputStream = new FileOutputStream(convertedFile);
        fileOutputStream.write(multipartFile.getBytes());
        fileOutputStream.close();
        return convertedFile;
    }

    public UploadResponse read(MultipartFile multipartFile, String username) {
        try {
            Tesseract tesseract = new Tesseract();
            tesseract.setDatapath("/usr/share/tesseract-ocr/4.00/tessdata");
            tesseract.setTessVariable("user_defined_dpi", "300");

            File convertedFile = this.convert(multipartFile);
            Tika tika = new Tika();
            String mimeType = tika.detect(convertedFile);

            if (mimeType.equals("image/png") || mimeType.equals("image/jpeg")
                    || mimeType.equals("image/jpg") || mimeType.equals("image/jfif")
                    || mimeType.equals("image/tiff")){
                InputStream is = new ByteArrayInputStream(multipartFile.getBytes());
                BufferedImage bi = ImageIO.read(is);
                return this.doOCR(tesseract, bi, username);
            }else if(mimeType.equals("application/pdf")){
                PDDocument document = PDDocument.load(convertedFile);
                PDFRenderer pdfRenderer = new PDFRenderer(document);
                for (int page = 0; page < document.getNumberOfPages(); ++page) {
                    BufferedImage bi = pdfRenderer.renderImageWithDPI(page, 300, ImageType.RGB);
                    this.doOCR(tesseract, bi, username);
                }
                document.close();
                return new UploadResponse(null, true, "PDF pages saved", username, new Date());
            }else {
                return new UploadResponse(null, false, "Unsupported file format", username, new Date());
            }


        }catch (IOException | TesseractException e){
            e.printStackTrace();
            return new UploadResponse(null, false, "An error occurred during read", username, new Date());
        }

    }

    private UploadResponse doOCR(
            Tesseract tesseract, BufferedImage bufferedImage, String username
    ) throws TesseractException{
        String result = tesseract.doOCR(bufferedImage);
        UploadResponse uploadResponse = new UploadResponse();
        UUID uuid = UUID.randomUUID();
        uploadResponse.setId(uuid.toString());
        uploadResponse.setStatus(true);
        uploadResponse.setText(result);
        uploadResponse.setUsername(username);
        uploadResponse.setTimestamp(new Date());
        uploadResponseRepository.save(uploadResponse);
        return uploadResponse;
    }

    public List<UploadResponse> getUserUploads(String username){
        return uploadResponseRepository.findByUsername(username);
    }

    public int totalUploads(String username){
        return getUserUploads(username).size();
    }

    public List<UploadResponse> uploadsFromDaysAgo(String username, Date startDate){
        return uploadResponseRepository.findByUsernameAndTimestampGreaterThan(username, startDate);
    }

    public int countUploadsFrom7DaysAgo(String username, Date startDate){
        return uploadsFromDaysAgo(username, startDate).size();
    }
}
