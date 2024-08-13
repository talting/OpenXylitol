package cc.xylitol.ui.gui.alt;


public abstract class Alt {
    private final String userName;
    private final AccountEnum accountType;

    public Alt(String userName,AccountEnum accountType) {
        this.userName = userName;
        this.accountType = accountType;
    }

    public AccountEnum getAccountType() {
        return accountType;
    }

    public String getUserName() {
        return userName;
    }
}
