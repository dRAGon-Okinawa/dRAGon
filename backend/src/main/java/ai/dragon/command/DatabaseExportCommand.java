package ai.dragon.command;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import ai.dragon.service.DatabaseService;

@Component
public class DatabaseExportCommand implements ApplicationRunner {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private DatabaseService databaseService;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        if (args.containsOption("command") && args.containsOption("database-export")) {
            logger.info("Database export command received");
            
            if(!args.containsOption("output")) {
                logger.error("Database export path not provided : --output=/tmp/dump.json");
                System.exit(1);
            }

            databaseService.exportDatabase(args.getOptionValues("output").get(0));
            logger.info("Database export completed");

            System.exit(0);
        }
    }
}
