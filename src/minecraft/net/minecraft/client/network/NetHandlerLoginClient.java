package net.minecraft.client.network;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.exceptions.AuthenticationException;
import com.mojang.authlib.exceptions.AuthenticationUnavailableException;
import com.mojang.authlib.exceptions.InvalidCredentialsException;
import com.mojang.authlib.minecraft.MinecraftSessionService;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.math.BigInteger;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.security.PublicKey;
import javax.crypto.SecretKey;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiDisconnected;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.network.EnumConnectionState;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.login.INetHandlerLoginClient;
import net.minecraft.network.login.client.C01PacketEncryptionResponse;
import net.minecraft.network.login.server.S00PacketDisconnect;
import net.minecraft.network.login.server.S01PacketEncryptionRequest;
import net.minecraft.network.login.server.S02PacketLoginSuccess;
import net.minecraft.network.login.server.S03PacketEnableCompression;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.CryptManager;
import net.minecraft.util.IChatComponent;
import net.augustus.utils.Logger;

public class NetHandlerLoginClient implements INetHandlerLoginClient {
   private static final Logger logger = new Logger();
   private final Minecraft mc;
   private final GuiScreen previousGuiScreen;
   private final NetworkManager networkManager;
   private GameProfile gameProfile;

   public NetHandlerLoginClient(NetworkManager p_i45059_1_, Minecraft mcIn, GuiScreen p_i45059_3_) {
      this.networkManager = p_i45059_1_;
      this.mc = mcIn;
      this.previousGuiScreen = p_i45059_3_;
   }

   @Override
   public void handleEncryptionRequest(S01PacketEncryptionRequest packetIn) {
      final SecretKey secretkey = CryptManager.createNewSharedKey();
      String s = packetIn.getServerId();
      PublicKey publickey = packetIn.getPublicKey();
      String s1 = new BigInteger(CryptManager.getServerIdHash(s, publickey, secretkey)).toString(16);

      if (this.mc.getCurrentServerData() != null && this.mc.getCurrentServerData().func_181041_d()) {
         try {
            this.getSessionService().joinServer(this.mc.getSession().getProfile(), this.mc.getSession().getToken(), s1);
         } catch (AuthenticationException var19) {
            logger.warn("Couldn't connect to auth servers but will continue to join LAN");
         }
      } else {
         try {
            this.getSessionService().joinServer(this.mc.getSession().getProfile(), this.mc.getSession().getToken(), s1);
         } catch (AuthenticationUnavailableException var16) {
            this.networkManager
               .closeChannel(
                  new ChatComponentTranslation("disconnect.loginFailedInfo", new ChatComponentTranslation("disconnect.loginFailedInfo.serversUnavailable"))
               );
            return;
         } catch (InvalidCredentialsException var17) {
            this.networkManager
               .closeChannel(
                  new ChatComponentTranslation("disconnect.loginFailedInfo", new ChatComponentTranslation("disconnect.loginFailedInfo.invalidSession"))
               );
            return;
         } catch (AuthenticationException var18) {
            this.networkManager.closeChannel(new ChatComponentTranslation("disconnect.loginFailedInfo", var18.getMessage()));
            return;
         }
      }

      this.networkManager
         .sendPacket(new C01PacketEncryptionResponse(secretkey, publickey, packetIn.getVerifyToken()), new GenericFutureListener<Future<? super Void>>() {
            @Override
            public void operationComplete(Future<? super Void> p_operationComplete_1_) throws Exception {
               NetHandlerLoginClient.this.networkManager.enableEncryption(secretkey);
            }
         });
   }

   private MinecraftSessionService getSessionService() {
      return this.mc.getSessionService();
   }

   @Override
   public void handleLoginSuccess(S02PacketLoginSuccess packetIn) {
      this.gameProfile = packetIn.getProfile();
      this.networkManager.setConnectionState(EnumConnectionState.PLAY);
      this.networkManager.setNetHandler(new NetHandlerPlayClient(this.mc, this.previousGuiScreen, this.networkManager, this.gameProfile));
   }

   @Override
   public void onDisconnect(IChatComponent reason) {
      this.mc.displayGuiScreen(new GuiDisconnected(this.previousGuiScreen, "connect.failed", reason));
   }

   @Override
   public void handleDisconnect(S00PacketDisconnect packetIn) {
      this.networkManager.closeChannel(packetIn.func_149603_c());
   }

   @Override
   public void handleEnableCompression(S03PacketEnableCompression packetIn) {
      if (!this.networkManager.isLocalChannel()) {
         this.networkManager.setCompressionTreshold(packetIn.getCompressionTreshold());
      }
   }
}
