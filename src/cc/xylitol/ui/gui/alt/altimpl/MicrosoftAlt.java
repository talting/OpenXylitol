package cc.xylitol.ui.gui.alt.altimpl;


import cc.xylitol.ui.gui.alt.AccountEnum;
import cc.xylitol.ui.gui.alt.Alt;

public final class MicrosoftAlt extends Alt {
    private final String refreshToken;

    public MicrosoftAlt(String userName,String refreshToken) {
        super(userName, AccountEnum.MICROSOFT);
        this.refreshToken = refreshToken;
    }

    public String getRefreshToken() {
        return refreshToken;
    }
}
