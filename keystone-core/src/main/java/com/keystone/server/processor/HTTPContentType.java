package com.keystone.server.processor;

import java.util.HashMap;
import java.util.Map;

/**
 * 
 * @author wuqq
 *
 */
public class HTTPContentType {
	
	private static Map<String, String> mapping = new HashMap<String, String>() ;
	
	
	/**
	 * 
	 * @param fileType
	 * @return
	 */
	public static String getContentType(String fileType) {
		return mapping.get(fileType) ;
	}
	
	
	
	
	static {
		mapping.put("ez","application/andrew-inset");
		mapping.put("hqx","application/mac-binhex40");
		mapping.put("cpt","application/mac-compactpro");
		mapping.put("doc","application/msword");
		mapping.put("bin","application/octet-stream");
		mapping.put("dms","application/octet-stream");
		mapping.put("lha","application/octet-stream");
		mapping.put("lzh","application/octet-stream");
		mapping.put("exe","application/octet-stream");
		mapping.put("class","application/octet-stream");
		mapping.put("so","application/octet-stream");
		mapping.put("dll","application/octet-stream");
		mapping.put("oda","application/oda");
		mapping.put("pdf","application/pdf");
		mapping.put("ai","application/postscript");
		mapping.put("eps","application/postscript");
		mapping.put("ps","application/postscript");
		mapping.put("smi","application/smil");
		mapping.put("smil","application/smil");
		mapping.put("mif","application/vnd.mif");
		mapping.put("xls","application/vnd.ms-excel");
		mapping.put("ppt","application/vnd.ms-powerpoint");
		mapping.put("wbxml","application/vnd.wap.wbxml");
		mapping.put("wmlc","application/vnd.wap.wmlc");
		mapping.put("wmlsc","application/vnd.wap.wmlscriptc");
		mapping.put("bcpio","application/x-bcpio");
		mapping.put("vcd","application/x-cdlink");
		mapping.put("pgn","application/x-chess-pgn");
		mapping.put("cpio","application/x-cpio");
		mapping.put("csh","application/x-csh");
		mapping.put("dcr","application/x-director");
		mapping.put("dir","application/x-director");
		mapping.put("dxr","application/x-director");
		mapping.put("dvi","application/x-dvi");
		mapping.put("spl","application/x-futuresplash");
		mapping.put("gtar","application/x-gtar");
		mapping.put("hdf","application/x-hdf");
		mapping.put("js","application/x-javascript");
		mapping.put("skp","application/x-koan");
		mapping.put("skd","application/x-koan");
		mapping.put("skt","application/x-koan");
		mapping.put("skm","application/x-koan");
		mapping.put("latex","application/x-latex");
		mapping.put("nc","application/x-netcdf");
		mapping.put("cdf","application/x-netcdf");
		mapping.put("sh","application/x-sh");
		mapping.put("shar","application/x-shar");
		mapping.put("swf","application/x-shockwave-flash");
		mapping.put("sit","application/x-stuffit");
		mapping.put("sv4cpio","application/x-sv4cpio");
		mapping.put("sv4crc","application/x-sv4crc");
		mapping.put("tar","application/x-tar");
		mapping.put("tcl","application/x-tcl");
		mapping.put("tex","application/x-tex");
		mapping.put("texinfo","application/x-texinfo");
		mapping.put("texi","application/x-texinfo");
		mapping.put("t","application/x-troff");
		mapping.put("tr","application/x-troff");
		mapping.put("roff","application/x-troff");
		mapping.put("man","application/x-troff-man");
		mapping.put("me","application/x-troff-me");
		mapping.put("ms","application/x-troff-ms");
		mapping.put("ustar","application/x-ustar");
		mapping.put("src","application/x-wais-source");
		mapping.put("xhtml","application/xhtml+xml");
		mapping.put("xht","application/xhtml+xml");
		mapping.put("zip","application/zip");
		mapping.put("au","audio/basic");
		mapping.put("snd","audio/basic");
		mapping.put("mid","audio/midi");
		mapping.put("midi","audio/midi");
		mapping.put("kar","audio/midi");
		mapping.put("mpga","audio/mpeg");
		mapping.put("mp2","audio/mpeg");
		mapping.put("mp3","audio/mpeg");
		mapping.put("aif","audio/x-aiff");
		mapping.put("aiff","audio/x-aiff");
		mapping.put("aifc","audio/x-aiff");
		mapping.put("m3u","audio/x-mpegurl");
		mapping.put("ram","audio/x-pn-realaudio");
		mapping.put("rm","audio/x-pn-realaudio");
		mapping.put("rpm","audio/x-pn-realaudio-plugin");
		mapping.put("ra","audio/x-realaudio");
		mapping.put("wav","audio/x-wav");
		mapping.put("pdb","chemical/x-pdb");
		mapping.put("xyz","chemical/x-xyz");
		mapping.put("bmp","image/bmp");
		mapping.put("gif","image/gif");
		mapping.put("ief","image/ief");
		mapping.put("jpeg","image/jpeg");
		mapping.put("jpg","image/jpeg");
		mapping.put("jpe","image/jpeg");
		mapping.put("png","image/png");
		mapping.put("tiff","image/tiff");
		mapping.put("tif","image/tiff");
		mapping.put("djvu","image/vnd.djvu");
		mapping.put("djv","image/vnd.djvu");
		mapping.put("wbmp","image/vnd.wap.wbmp");
		mapping.put("ras","image/x-cmu-raster");
		mapping.put("pnm","image/x-portable-anymap");
		mapping.put("pbm","image/x-portable-bitmap");
		mapping.put("pgm","image/x-portable-graymap");
		mapping.put("ppm","image/x-portable-pixmap");
		mapping.put("rgb","image/x-rgb");
		mapping.put("xbm","image/x-xbitmap");
		mapping.put("xpm","image/x-xpixmap");
		mapping.put("xwd","image/x-xwindowdump");
		mapping.put("igs","model/iges");
		mapping.put("iges","model/iges");
		mapping.put("msh","model/mesh");
		mapping.put("mesh","model/mesh");
		mapping.put("silo","model/mesh");
		mapping.put("wrl","model/vrml");
		mapping.put("vrml","model/vrml");
		mapping.put("css","text/css");
		mapping.put("html","text/html");
		mapping.put("htm","text/html");
		mapping.put("asc","text/plain");
		mapping.put("txt","text/plain");
		mapping.put("rtx","text/richtext");
		mapping.put("rtf","text/rtf");
		mapping.put("sgml","text/sgml");
		mapping.put("sgm","text/sgml");
		mapping.put("tsv","text/tab-separated-values");
		mapping.put("wml","text/vnd.wap.wml");
		mapping.put("wmls","text/vnd.wap.wmlscript");
		mapping.put("etx","text/x-setext");
		mapping.put("xsl","text/xml");
		mapping.put("xml","text/xml");
		mapping.put("mpeg","video/mpeg");
		mapping.put("mpg","video/mpeg");
		mapping.put("mpe","video/mpeg");
		mapping.put("qt","video/quicktime");
		mapping.put("mov","video/quicktime");
		mapping.put("mxu","video/vnd.mpegurl");
		mapping.put("avi","video/x-msvideo");
		mapping.put("movie","video/x-sgi-movie");
		mapping.put("ice","x-conference/x-cooltalk");
		mapping.put("json","application/json"); 
		
		
		//自定义支持
		mapping.put("mgr","text/html");
		mapping.put("do","text/html");
	}
}
