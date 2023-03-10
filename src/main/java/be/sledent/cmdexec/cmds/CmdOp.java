package be.sledent.cmdexec.cmds;

@FunctionalInterface
public interface CmdOp {
    int run(String ...args) throws Exception;
}
