package it.telecomitalia.timcoupon.filetransfer;

public class BFileTransferFactory
{
	public static enum TFileTransferMethods
	{
		// EMethodFTP,
		// EMethodSFTP,
		EMethodHTTP
	};

	public static IFileTransferMethod createFileTransferMethod(String host, TFileTransferMethods method)
	{
		IFileTransferMethod newMethod = null;
		switch (method)
		{
		/*
		 * case EMethodFTP: { newMethod = getFTPMethod(); } break; case
		 * EMethodSFTP: { newMethod = getSFTPMethod(host); } break;
		 */
			case EMethodHTTP:
				{
					newMethod = getHTTPMethod();
				}
				break;
		}
		return newMethod;
	}

	private static HTTPFileTransferMethod getHTTPMethod()
	{
		HTTPFileTransferMethod method = new HTTPFileTransferMethod();
		return method;
	}

	/*
	 * private static SFTPMethod getSFTPMethod(String host) { SFTPMethod method
	 * = new SFTPMethod(host); return method; }
	 * 
	 * private static FTPMethod getFTPMethod() { FTPMethod method = new
	 * FTPMethod(); return method; }
	 */
}
