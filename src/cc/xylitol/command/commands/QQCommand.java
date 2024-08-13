package cc.xylitol.command.commands;

import cc.xylitol.Client;
import cc.xylitol.command.Command;
import cc.xylitol.utils.DebugUtil;
import cc.xylitol.utils.Multithreading;
import cc.xylitol.utils.WebUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class QQCommand extends Command {
    private static final Pattern PATTERN = Pattern.compile("\"(qq\":\"\\d+)\",\"(phone\":\"\\d+)\",\"(.*)");

    public QQCommand() {
        super("qq");
    }

    @Override
    public List<String> autoComplete(int arg, String[] args) {
        return new ArrayList<>();
    }

    @Override
    public void run(String[] args, String originalMessag) {
        if (args.length < 1) {
            DebugUtil.log(".qq <number>");
            return;
        }

        Multithreading.run(() -> {
            DebugUtil.log("Getting information...");
            String result = WebUtils.get("https://zy.xywlapi.cc/qqapi?qq=" + args[0]);

            assert result != null;
            Matcher matcher = PATTERN.matcher(result);

            if (matcher.find()) {
                DebugUtil.log("§6§oQQ: " + matcher.group(1).substring(5));
                DebugUtil.log("§6§oPhone: " + matcher.group(2).substring(8));
                DebugUtil.log("§6§oAddress: " + matcher.group(3).substring(12, 20));
                DebugUtil.log("§aStatus: OK");
            } else {
                DebugUtil.log("§cFailed to get QQ information!");
            }
        });
    }
}
