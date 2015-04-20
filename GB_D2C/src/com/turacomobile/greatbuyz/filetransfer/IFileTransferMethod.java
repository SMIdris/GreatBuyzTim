package com.turacomobile.greatbuyz.filetransfer;

public interface IFileTransferMethod
{
	public boolean uploadFile(String host, String username, String password, int port, String srcFileDirectory, String srcFileName,
			String desFileDirectory, String desFileName);
}