package tn.esprit.pi.controllers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import tn.esprit.pi.services.MetaDataService;
@CrossOrigin(origins = "http://localhost:4200")
@RestController
@RequestMapping("/Automation")
public class FileContrloller {

    @Autowired
    public MetaDataService metadataService;
    @Autowired
    public SimpMessagingTemplate messagingTemplate;
    @PostMapping("/upload")
    public String handleFileUpload(@RequestParam("file") MultipartFile file) {
        metadataService.processFile(file);
        messagingTemplate.convertAndSend("/topic/notifications", "File uploaded successfully!");
        return "File uploaded successfully!";
    }


}





