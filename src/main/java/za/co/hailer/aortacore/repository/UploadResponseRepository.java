package za.co.hailer.aortacore.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import za.co.hailer.aortacore.model.UploadResponse;

import java.util.Date;
import java.util.List;

public interface UploadResponseRepository extends MongoRepository<UploadResponse, String> {
    List<UploadResponse> findByUsername(String username);
    List<UploadResponse> findByUsernameAndTimestampGreaterThan(String username, Date timestamp);
}
