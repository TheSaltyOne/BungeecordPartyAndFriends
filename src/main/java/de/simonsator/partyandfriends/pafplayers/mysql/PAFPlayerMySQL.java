package de.simonsator.partyandfriends.pafplayers.mysql;

import de.simonsator.partyandfriends.api.PermissionProvider;
import de.simonsator.partyandfriends.api.events.PAFAccountDeleteEvent;
import de.simonsator.partyandfriends.api.pafplayers.IDBasedPAFPlayer;
import de.simonsator.partyandfriends.api.pafplayers.PAFPlayer;
import de.simonsator.partyandfriends.api.pafplayers.PAFPlayerClass;
import de.simonsator.partyandfriends.api.pafplayers.PAFPlayerManager;
import de.simonsator.partyandfriends.pafplayers.manager.PAFPlayerManagerMySQL;
import net.md_5.bungee.api.ProxyServer;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class PAFPlayerMySQL extends PAFPlayerClass implements IDBasedPAFPlayer {
	private final int ID;

	public PAFPlayerMySQL(int pID) {
		ID = pID;
	}

	@Override
	public String getName() {
		return PAFPlayerManagerMySQL.getConnection().getName(ID);
	}

	public int getPlayerID() {
		return ID;
	}

	@Override
	public List<PAFPlayer> getFriends() {
		return idListToPAFPlayerList(PAFPlayerManagerMySQL.getConnection().getFriends(ID));
	}

	@Override
	public UUID getUniqueId() {
		return PAFPlayerManagerMySQL.getConnection().getUUID(ID);
	}

	@Override
	public String toString() {
		return "{Name:\"" + getName() + "\", DisplayName:\"" + getDisplayName() + "\"}";
	}

	@Override
	public boolean doesExist() {
		return ID > 0;
	}

	@Override
	public int getSettingsWorth(int pSettingsID) {
		return PAFPlayerManagerMySQL.getConnection().getSettingsWorth(ID, pSettingsID);
	}

	@Override
	public List<PAFPlayer> getRequests() {
		return idListToPAFPlayerList(PAFPlayerManagerMySQL.getConnection().getRequests(ID));
	}

	@Override
	public boolean hasRequestFrom(PAFPlayer pPlayer) {
		return PAFPlayerManagerMySQL.getConnection().hasRequestFrom(ID, ((PAFPlayerMySQL) pPlayer.getPAFPlayer()).getPlayerID());
	}

	@Override
	public boolean hasPermission(String pPermission) {
		return pPermission == null || pPermission.isEmpty() || PermissionProvider.getInstance().hasPermission(this, pPermission);
	}

	@Override
	public void denyRequest(PAFPlayer pPlayer) {
		PAFPlayerManagerMySQL.getConnection().denyRequest(ID, ((PAFPlayerMySQL) pPlayer.getPAFPlayer()).getPlayerID());
	}

	@Override
	public boolean isAFriendOf(PAFPlayer pPlayer) {
		return PAFPlayerManagerMySQL.getConnection().isAFriendOf(ID, ((PAFPlayerMySQL) pPlayer.getPAFPlayer()).getPlayerID());
	}

	private List<PAFPlayer> idListToPAFPlayerList(List<Integer> pList) {
		List<PAFPlayer> list = new ArrayList<>();
		for (int playerID : pList)
			list.add(((PAFPlayerManagerMySQL) PAFPlayerManager.getInstance()).getPlayer(playerID));
		return list;
	}

	@Override
	public PAFPlayer getLastPlayerWroteTo() {
		return ((PAFPlayerManagerMySQL) PAFPlayerManager.getInstance()).getPlayer(PAFPlayerManagerMySQL.getConnection().getLastPlayerWroteTo(ID));
	}

	@Override
	public void sendFriendRequest(PAFPlayer pSender) {
		PAFPlayerManagerMySQL.getConnection().sendFriendRequest(((PAFPlayerMySQL) pSender).getPlayerID(), ID);
	}

	@Override
	public void addFriend(PAFPlayer pPlayer) {
		PAFPlayerManagerMySQL.getConnection().addFriend(((PAFPlayerMySQL) pPlayer.getPAFPlayer()).getPlayerID(), ID);
	}

	@Override
	public PAFPlayer getPAFPlayer() {
		return this;
	}

	@Override
	public void removeFriend(PAFPlayer pPlayer) {
		PAFPlayerManagerMySQL.getConnection().deleteFriend(((PAFPlayerMySQL) pPlayer.getPAFPlayer()).getPlayerID(), ID);
	}

	@Override
	public void setSetting(int pSettingsID, int pNewWorth) {
		PAFPlayerManagerMySQL.getConnection().setSetting(ID, pSettingsID, pNewWorth);
	}

	@Override
	public void setLastPlayerWroteFrom(PAFPlayer pLastWroteTo) {
		PAFPlayerManagerMySQL.getConnection().setLastPlayerWroteTo(ID, ((PAFPlayerMySQL) pLastWroteTo.getPAFPlayer())
				.getPlayerID(), 0);
	}

	@Override
	public long getLastOnline() {
		Timestamp time = PAFPlayerManagerMySQL.getConnection().getLastOnline(ID);
		if (time != null)
			return time.getTime();
		return 0;
	}

	@Override
	public boolean deleteAccount() {
		PAFAccountDeleteEvent event = new PAFAccountDeleteEvent(this);
		ProxyServer.getInstance().getPluginManager().callEvent(event);
		if (!event.isCancelled()) {
			PAFPlayerManagerMySQL.getConnection().deletePlayerEntry(ID);
			return true;
		}
		return false;
	}

	@Override
	public void updateLastOnline() {
		PAFPlayerManagerMySQL.getConnection().updateLastOnline(getPlayerID());
	}
}
