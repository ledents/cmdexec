package be.sledent.cmdexec.cmds;


import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;


import java.util.Hashtable;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

@Slf4j
@Component("CmdRouter")
public class CmdRouter implements CmdOp {
    public static final String DEFAUTLT_CMDID = ".";
    Hashtable<String, CmdOp> opIndex;
    CompletableFuture<Integer> retc;
    public CmdRouter() {
        opIndex = new Hashtable<>();
        opIndex.put("dump-args", CmdRouter::dumpArgs);
        opIndex.put("info",      CmdRouter::info);
        opIndex.put(DEFAUTLT_CMDID,  CmdRouter::info);
        retc = new CompletableFuture<>();
    }
    @Override
    public int run(String... args) throws Exception {
        CmdOp cmdOp = (args.length > 0) ? opIndex.get(args[0]) : null;
        if (cmdOp == null) {
            cmdOp = opIndex.get(DEFAUTLT_CMDID);
        } else {
            args = shift(args);
        }
        if (cmdOp == null) {
            System.err.println("No default command-line operation installed!");
            retc.complete(99);
            return 99;
        }
        
        try {
            int retv = cmdOp.run(args);
            retc.complete(retv);
            return retv;
        } catch (Exception e) {
            retc.complete(1);
            return 1;
        }
    }

    protected static int info(String ...args) {
        System.out.println("""
                 Syntax: [<cmd-id>] [arg-1] ... [arg-n] 
                 Where <cmd-id> can be one of:
                  info      : prints this help information message.
                  dump-args : dumps the arguments actually passed to the command.  
                """);
        return 0;
    }
    protected static int dumpArgs(String ...args) {
        int i=0;
        for (String arg : args) {
            System.out.println(String.format(" -%d: ->%s<", i++, arg));
        }
        return 0;
    }
    protected String[] shift(String ...args) {
        String[] newArgs = new String[args.length-1];
        for (int i=1; i<args.length; i++) {
            newArgs[i-1] = args[i];
        }
        return newArgs;
    }
    public int getReturnCode() throws ExecutionException, InterruptedException {
        return retc.get();
    }

    @Component
    protected class CommandLineRouter implements CommandLineRunner {

        @Override
        public void run(String... args) {
            try {
                CmdRouter.this.run(args);
            } catch (Exception e) {
                retc.completeExceptionally(e);
                e.printStackTrace();
            }
        }
    }
}
