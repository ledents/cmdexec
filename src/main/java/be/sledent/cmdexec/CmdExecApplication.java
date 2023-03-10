package be.sledent.cmdexec;

import be.sledent.cmdexec.cmds.CmdRouter;
import org.springframework.boot.Banner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

@SpringBootApplication
public class CmdExecApplication {

	public static void main(String[] args) {
		SpringApplication app = new SpringApplication(CmdExecApplication.class);
		app.setBannerMode(Banner.Mode.OFF);
		ApplicationContext ctx = app.run(args);
		CmdRouter router = (CmdRouter) ctx.getBean("CmdRouter");
		try {
			int exitCode = router.getReturnCode(); // new CmdRouter().run(args);
			SpringApplication.exit(ctx);
			System.exit(exitCode);
		} catch (Exception e) {
			SpringApplication.exit(ctx);
			System.exit(1);
		}

	}
}
