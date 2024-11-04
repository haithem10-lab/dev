
import com.opencsv.exceptions.CsvValidationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.web.multipart.MultipartFile;
import tn.esprit.pi.entities.FileMetaData;
import tn.esprit.pi.entities.MetaDataExtractor;
import tn.esprit.pi.services.MetaDataService;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class MetaDataServiceTest {

    @InjectMocks
    private MetaDataService metaDataService;

    @Mock
    private MongoTemplate mongoTemplate;

    @Mock
    private MultipartFile mockFile;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testProcessFile() throws IOException, CsvValidationException {
        // Prepare a mock file
        String fileName = "test.csv";
        byte[] content = "header1,header2\nvalue1,value2".getBytes();
        ByteArrayInputStream inputStream = new ByteArrayInputStream(content);

        when(mockFile.getOriginalFilename()).thenReturn(fileName);
        when(mockFile.getInputStream()).thenReturn(inputStream);
        when(mockFile.getSize()).thenReturn((long) content.length);

        // Simulate metadata extraction
        Map<String, String> mockMetadata = new HashMap<>();
        mockMetadata.put("header1", "value1");
        mockMetadata.put("header2", "value2");

        // Here you would need to mock the MetaDataExtractor
        when(MetaDataExtractor.extractMetadata(any(), any())).thenReturn(mockMetadata);

        // Call the method under test
        metaDataService.processFile(mockFile);

        // Verify that mongoTemplate.save was called with the expected FileMetaData
        FileMetaData expectedMetaData = new FileMetaData();
        expectedMetaData.setFileName(fileName);
        expectedMetaData.setFileType("CSV");
        expectedMetaData.setMetadata(mockMetadata);

        verify(mongoTemplate, times(1)).save(expectedMetaData);
        // Optionally, you can also check processedFiles
        assert(metaDataService.getProcessedFiles().contains(mockFile));
    }

    @Test
    void testProcessFileIOException() throws IOException {
        when(mockFile.getOriginalFilename()).thenReturn("test.csv");
        when(mockFile.getInputStream()).thenThrow(new IOException("Simulated IO exception"));

        // Call the method under test
        metaDataService.processFile(mockFile);

        // Verify that mongoTemplate.save was never called
        verify(mongoTemplate, never()).save(any());
    }

    // Add more tests for other methods as needed
}
