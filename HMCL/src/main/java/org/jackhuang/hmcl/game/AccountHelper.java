/*
 * Hello Minecraft! Launcher.
 * Copyright (C) 2017  huangyuhui <huanghongxun2008@126.com>
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see {http://www.gnu.org/licenses/}.
 */
package org.jackhuang.hmcl.game;

import javafx.geometry.Rectangle2D;
import javafx.scene.image.Image;
import org.jackhuang.hmcl.Main;
import org.jackhuang.hmcl.auth.Account;
import org.jackhuang.hmcl.auth.yggdrasil.GameProfile;
import org.jackhuang.hmcl.auth.yggdrasil.YggdrasilAccount;
import org.jackhuang.hmcl.setting.Settings;
import org.jackhuang.hmcl.task.FileDownloadTask;
import org.jackhuang.hmcl.task.Scheduler;
import org.jackhuang.hmcl.task.Schedulers;
import org.jackhuang.hmcl.task.Task;
import org.jackhuang.hmcl.ui.DialogController;
import org.jackhuang.hmcl.ui.FXUtilsKt;
import org.jackhuang.hmcl.util.NetworkUtils;

import java.io.File;
import java.net.Proxy;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

public final class AccountHelper {
    public static final AccountHelper INSTANCE = new AccountHelper();
    private AccountHelper() {}

    public static final File SKIN_DIR = new File(Main.APPDATA, "skins");

    public static void loadSkins() {
        loadSkins(Proxy.NO_PROXY);
    }

    public static void loadSkins(Proxy proxy) {
        for (Account account : Settings.INSTANCE.getAccounts().values()) {
            if (account instanceof YggdrasilAccount) {
                new SkinLoadTask((YggdrasilAccount) account, proxy, false).start();
            }
        }
    }
    public static Task loadSkinAsync(YggdrasilAccount account) {
        return loadSkinAsync(account, Settings.INSTANCE.getProxy());
    }

    public static Task loadSkinAsync(YggdrasilAccount account, Proxy proxy) {
        return new SkinLoadTask(account, proxy, false);
    }

    public static Task refreshSkinAsync(YggdrasilAccount account) {
        return refreshSkinAsync(account, Settings.INSTANCE.getProxy());
    }

    public static Task refreshSkinAsync(YggdrasilAccount account, Proxy proxy) {
        return new SkinLoadTask(account, proxy, true);
    }

    private static File getSkinFile(String name) {
        return new File(SKIN_DIR, name + ".png");
    }

    public static Image getSkin(YggdrasilAccount account) {
        return getSkin(account, 1);
    }

    public static Image getSkin(YggdrasilAccount account, double scaleRatio) {
        if (account.getSelectedProfile() == null) return FXUtilsKt.DEFAULT_ICON;
        String name = account.getSelectedProfile().getName();
        if (name == null) return FXUtilsKt.DEFAULT_ICON;
        File file = getSkinFile(name);
        if (file.exists()) {
            Image original = new Image("file:" + file.getAbsolutePath());
            return new Image("file:" + file.getAbsolutePath(),
                    original.getWidth() * scaleRatio,
                    original.getHeight() * scaleRatio,
                    false, false);
        }
        return FXUtilsKt.DEFAULT_ICON;
    }

    public static Rectangle2D getViewport(double scaleRatio) {
        double size = 8.0 * scaleRatio;
        return new Rectangle2D(size, size, size, size);
    }

    private static class SkinLoadTask extends Task {
        private final YggdrasilAccount account;
        private final Proxy proxy;
        private final boolean refresh;
        private final List<Task> dependencies = new LinkedList<>();

        public SkinLoadTask(YggdrasilAccount account, Proxy proxy) {
            this(account, proxy, false);
        }

        public SkinLoadTask(YggdrasilAccount account, Proxy proxy, boolean refresh) {
            this.account = account;
            this.proxy = proxy;
            this.refresh = refresh;
        }

        @Override
        public Scheduler getScheduler() {
            return Schedulers.io();
        }

        @Override
        public Collection<Task> getDependencies() {
            return dependencies;
        }

        @Override
        public void execute() throws Exception {
            if (account.canLogIn() && (account.getSelectedProfile() == null || refresh))
                DialogController.INSTANCE.logIn(account);

            GameProfile profile = account.getSelectedProfile();
            if (profile == null) return;
            String name = profile.getName();
            if (name == null) return;
            String url = "http://skins.minecraft.net/MinecraftSkins/" + name + ".png";
            File file = getSkinFile(name);
            if (!refresh && file.exists())
                return;
            dependencies.add(new FileDownloadTask(NetworkUtils.toURL(url), file, proxy));
        }
    }
}
