package org.monarchinitiative.ast;
import org.monarchinitiative.ast.cmd.AssessSufficiencyCommand;
import org.monarchinitiative.ast.cmd.DownloadCommand;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import picocli.CommandLine;
import java.util.concurrent.Callable;

/**
 *
 *
 */

@CommandLine.Command(name = "java -jar AnnotationSufficiencyTool.jar", mixinStandardHelpOptions = true,
        version = "0.0.1",
        description = "Annotation Sufficiency Tool")
public class Ast implements Callable<Integer> {
    private static final Logger logger = LoggerFactory.getLogger(Ast.class);
    public static void main( String[] args )
    {
        if (args.length == 0) {
            // if the user doesn't pass any command or option, add -h to show help
            args = new String[]{"-h"};
        }
        long startTime = System.currentTimeMillis();
        CommandLine cline = new CommandLine(new Ast())
                .addSubcommand("download", new DownloadCommand())
                .addSubcommand("assess", new AssessSufficiencyCommand());
        cline.setToggleBooleanFlags(false);
        int exitCode = cline.execute(args);
        long stopTime = System.currentTimeMillis();
        int elapsedTime = (int)((stopTime - startTime)*(1.0)/1000);
        if (elapsedTime > 3599) {
            int elapsedSeconds = elapsedTime % 60;
            int elapsedMinutes = (elapsedTime/60) % 60;
            int elapsedHours = elapsedTime/3600;
            logger.info(String.format("Elapsed time %d:%2d%2d",elapsedHours,elapsedMinutes,elapsedSeconds));
        }
        else if (elapsedTime>59) {
            int elapsedSeconds = elapsedTime % 60;
            int elapsedMinutes = (elapsedTime/60) % 60;
            logger.info(String.format("Elapsed time %d min, %d sec",elapsedMinutes,elapsedSeconds));
        } else {
            logger.info("Elapsed time " + (stopTime - startTime) * (1.0) / 1000 + " seconds.");
        }
        System.exit(exitCode);
    }

    @Override
    public Integer call() {
        // work done in subcommands
        return 0;
    }
}
