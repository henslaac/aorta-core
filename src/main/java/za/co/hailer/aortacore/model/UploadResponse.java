package za.co.hailer.aortacore.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UploadResponse {
    @Id
    private String id;
    private boolean status;
    private String text;
    private String username;
    private Date timestamp;
}
