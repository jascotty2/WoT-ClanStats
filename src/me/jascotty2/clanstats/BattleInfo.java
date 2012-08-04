/**
 * Copyright (C) 2012 Jacob Scott <jascottytechie@gmail.com>
 * Description: Defines a Province
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
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package me.jascotty2.clanstats;

import java.util.ArrayList;
import me.jascotty2.lib.io.CheckInput;

public class BattleInfo {
	public String type, provinceName, provinceID;
	public ArrayList<String> arenas = new ArrayList<String>();
	public boolean started;
	public long battleTime; // when battle is scheduled

	public void setBattleTime(String battleTime) {
		this.battleTime = (long) CheckInput.GetDouble(battleTime, 0);
	}
}
