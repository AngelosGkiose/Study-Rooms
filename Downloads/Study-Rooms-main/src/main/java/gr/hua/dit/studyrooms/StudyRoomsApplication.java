package gr.hua.dit.studyrooms;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;

@SpringBootApplication
@EntityScan("gr.hua.dit.studyrooms.core.model")  // <-- Προσθήκη
public class StudyRoomsApplication {
    public static void main(String[] args) {
        SpringApplication.run(StudyRoomsApplication.class, args);
    }
}
