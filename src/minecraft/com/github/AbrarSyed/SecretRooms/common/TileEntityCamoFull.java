// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) braces deadcode

package com.github.AbrarSyed.SecretRooms.common;

import java.io.ByteArrayOutputStream;
import java.io.ObjectOutputStream;

import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.Packet250CustomPayload;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

/**
 * @author AbrarSyed
 */
public class TileEntityCamoFull extends TileEntity
{
	public TileEntityCamoFull()
	{
		super();
		holder = null;
	}

	@Override
	public boolean canUpdate()
	{
		return false;
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt)
	{
		super.readFromNBT(nbt);
		
		// backwards compat...
		if (nbt.hasKey("hasCoords"))
		{
			boolean hasCoords = nbt.getBoolean("hasCoords");
			if (hasCoords)
			{
				int x = nbt.getInteger("copyCoordX");
				int y = nbt.getInteger("copyCoordY");
				int z = nbt.getInteger("copyCoordZ");
				
				holder = new BlockHolder(worldObj, x, y, z);
			}
			else
			{
				int id = nbt.getInteger("copyID");
				holder = new BlockHolder(id, 0, null);
			}
		}
		else
			holder = BlockHolder.buildFromNBT(nbt);
	}

	@Override
	public void writeToNBT(NBTTagCompound nbttagcompound)
	{
		super.writeToNBT(nbttagcompound);
		if (holder != null)
		{
			holder.writeToNBT(nbttagcompound);
		}
		SecretRooms.proxy.getFakeWorld(worldObj).addOverrideBlock(xCoord, yCoord, zCoord, holder);
	}

	/**
	 * signs and mobSpawners use this to send text and meta-data
	 */
	@Override
	public Packet getDescriptionPacket()
	{
		Packet250CustomPayload packet = new Packet250CustomPayload();

		packet.isChunkDataPacket = true;

		packet.channel = "SRM-TE-CamoFull";

		ByteArrayOutputStream bytes = new ByteArrayOutputStream();
		try
		{
			ObjectOutputStream data = new ObjectOutputStream(bytes);
			int[] coords = { xCoord, yCoord, zCoord };
			for (int a = 0; a < coords.length; a++)
			{
				data.writeInt(coords[a]);
			}
			NBTTagCompound nbt = new NBTTagCompound();
			holder.writeToNBT(nbt);
			NBTBase.writeNamedTag(nbt, data);
			data.close();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}

		packet.data = bytes.toByteArray();

		packet.length = packet.data.length;
		return packet;
	}

	@Override
	public boolean shouldRefresh(int oldID, int newID, int oldMeta, int newMeta, World world, int x, int y, int z)
	{
		SecretRooms.proxy.getFakeWorld(world).removeOverrideBlock(x, y, z);
		return true;
	}

	public void setBlockHolder(BlockHolder holder)
	{
		if (holder == null)
			return;
		SecretRooms.proxy.getFakeWorld(worldObj).addOverrideBlock(xCoord, yCoord, zCoord, holder);
		this.holder = holder;
		worldObj.markBlockForRenderUpdate(xCoord, yCoord, zCoord);
	}

	public BlockHolder getBlockHolder()
	{
		return holder;
	}

	public int getCopyID()
	{
		return holder == null ? 0 : holder.blockID;
	}

	private BlockHolder	holder;

}