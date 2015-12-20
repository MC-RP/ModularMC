package org.modularmc.handlers;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.modularmc.Server;
import org.modularmc.game.Player;
import org.modularmc.game.world.Difficulty;
import org.modularmc.game.world.Dimension;
import org.modularmc.game.world.Gamemode;
import org.modularmc.network.Client;
import org.modularmc.network.Client.State;
import org.modularmc.network.packets.login.LoginSucessPacket;
import org.modularmc.network.packets.play.JoinGamePacket;
import org.modularmc.network.packets.play.player.ClientbondPlayerPositionAndLookPacket;
import org.modularmc.network.packets.play.world.SpawnPositionPacket;

/**
 * Handling Login AND joingame!
 * @author Caspar Nor�e Palm
 */
public class LoginHandler {
	
	final Server server;
	
	private List<Client> awatingLogin;
	
	public LoginHandler(final Server server) {
		this.server = server;
		awatingLogin = new ArrayList<>(4);
	}
	
	public void addClient(final Client c) {
		synchronized(awatingLogin) {
			awatingLogin.add(c);
		}
	}
	
	public void process() {
		Client[] clients;
		synchronized(awatingLogin) {
			clients = awatingLogin.toArray(new Client[awatingLogin.size()]); // Copies to a new array to keep lock on awatingLogin as short as possible 
			awatingLogin.clear();
		}
		if(clients.length == 0)
			return;

		for(Client c : clients)
			login(c);
	}
	
	private void login(Client c) {
		System.out.println("Login");
		
		LoginSucessPacket sucess = new LoginSucessPacket();
		
		sucess.setUsername("Fozie");
		sucess.setUuid(UUID.randomUUID());
		
		c.sendPacket(sucess);
		c.setState(State.PLAY);
		
		JoinGamePacket join = new JoinGamePacket();
		join.setDifficulty(Difficulty.PEACEFUL);
		join.setDimension(Dimension.OVERWORLD);
		join.setEntityID(1337);
		join.setGamemode(Gamemode.SURVIVAL);
		
		c.sendPacket(join);
		
		SpawnPositionPacket spawnpos = new SpawnPositionPacket();
		
		c.sendPacket(spawnpos);
		
		ClientbondPlayerPositionAndLookPacket p = new ClientbondPlayerPositionAndLookPacket();
		p.setX(0);
		p.setY(100);
		p.setZ(0);
		
		p.setYaw(90);
		p.setPitch(0);
		
		c.sendPacket(p);
		c.sendPacket(p);
	}
}
