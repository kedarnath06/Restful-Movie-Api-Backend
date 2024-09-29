package com.MovieTicketApi.service.implementation;

import com.MovieTicketApi.service.serviceinterface.FileService;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;

@Service
public class FileServiceImp implements FileService {

    @Override
    public String uploadFile(String path, MultipartFile file) throws IOException {
        //get name of the file
        String fileName = file.getOriginalFilename();

        //get the filepath
        String filePath = path + File.separator + fileName;

        //create file obj
        File f = new File(path);
        if (!f.exists()) {
            f.mkdir();
        }

        //copy the file or upload file to the path
        Files.copy(file.getInputStream(), Paths.get(filePath));

        return fileName;
    }

    @Override
    public InputStream getResouceFile(String path, String fileName) throws FileNotFoundException {
        String filePath = path + File.separator +fileName;
        return new FileInputStream(filePath);
    }
}
