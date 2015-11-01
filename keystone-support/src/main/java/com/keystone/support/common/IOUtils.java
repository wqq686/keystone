package com.keystone.support.common;

import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.GatheringByteChannel;
import java.nio.charset.Charset;


public class IOUtils {
    /* ------------------------------------------------------------------- */
    public final static String CRLF      = "\015\012";

    /* ------------------------------------------------------------------- */
    public final static byte[] CRLF_BYTES    = {(byte)'\015',(byte)'\012'};

    /* ------------------------------------------------------------------- */
    public static final int bufferSize = 64*1024;
	
	/**
	 * 
	 * @param filename
	 * @return
	 */
	public static InputStream getInputStreamFromClassPath(String filename) {
		return Thread.currentThread().getContextClassLoader().getResourceAsStream(filename);
	}
	
	
	
	/**
	 * 
	 * @param file
	 * @return
	 */
	public static String getCanonicalPath(File file) {
		try
		{
			return file == null ? null : file.getCanonicalPath() ;
		}catch(Exception e){throw ExceptionUtils.illegalStateException(e);}
	}
	
	
	/**
	 * 以rootFile作为根路径, 合并多个路径
	 * @param rootFile
	 * @param paths
	 * @return
	 */
	public static String mergePaths(File rootFile, String...paths) {
		String[] array = paths ; String root = null ;
		if(rootFile!=null)
		{
			root = getCanonicalPath(rootFile) ;
			if(paths!=null && paths.length>1)
			{
				array = new String[paths.length+1] ;
				System.arraycopy(paths, 0, array, 1, paths.length) ;
			}
			else
			{
				array = new String[1] ;
			}
			array[0] = root ;
		}
		return mergePaths(array) ;
	}
	
	
	/**
	 * 合并多个路径
	 * 
	 * @param paths
	 * @return
	 */
	public static String mergePaths(String...paths) {
		if(CommonUtils.isEmpty(paths)) return null ;
		StringBuilder builder = new StringBuilder(paths[0]) ;
		for(int i=1; i<paths.length; i++)
		{
			String path = StringUtils.emptyOrTrim(paths[i]) ;
			path = path.startsWith(File.separator) ? path.substring(1) : path ;
			path = path.endsWith(File.separator) ? path.substring(0, path.length()-1) : path ;
			builder.append(File.separator).append(path) ;
		}
		String path = builder.toString() ;
		return path ;
	}
	
	
	
	/**
	 * 创建路径
	 * 
	 * @param dirs
	 * @return
	 */
	public static String mkdirs(String... dirs) {
		String pathname = mergePaths(dirs) ;
		if(pathname!=null) {
			File file = new File(pathname) ;
			if( !file.exists() ){
				file.mkdirs() ;
			}
		}
		return pathname ;
	}
	
	
	
	/**
	 * 根据路径获取文件, 如果文件不存在则创建文件
	 * @param pathname
	 * @return
	 */
	public static File getFile(String pathname) {
		try
		{
			File file = new File(pathname) ;
			if(!file.exists())
			{
				String parent = file.getParent() ;
				mkdirs(parent) ;
				file.createNewFile() ;
			}
			return file ;
		}catch(Exception e){throw ExceptionUtils.convertRuntimeException(e);}
	}
	
	
	
	/**
	 * 
	 * @param pathnames "c", "test", "readme.txt"
	 * @return
	 */
	public static File getFile(String... pathnames) {
		try
		{
			String[] dirs = new String[pathnames.length-1] ; String filename = pathnames[pathnames.length-1] ;
			
			System.arraycopy(pathnames, 0, dirs, 0, pathnames.length-1);
			String dirpath = mkdirs(dirs) ;
			String pathname = mergePaths(dirpath, filename) ;
			return getFile(pathname) ;
		}catch(Exception e){throw ExceptionUtils.convertRuntimeException(e);}
	}
	
	
	
	/**
	 * 重命名文件, 首先会调用current.renameTo(newFile), 如果失败会采用NIO transferTo的方式进行
	 * @param current
	 * @param newFile
	 */
	public static void renameFile(File current, File newFile) {
		try
		{
			boolean ok = current.renameTo(newFile) ;
			if(!ok)
			{
				FileChannel in = null, out = null ;
				try
				{
					in = new FileInputStream(current).getChannel();
					out = new FileOutputStream(newFile).getChannel();
					in.transferTo(0, in.size(), out);
					out.force(true) ;
					
					current = new File(current.getCanonicalPath()) ;
					if(current.exists()) {
						current.delete() ;
					}
				} finally { close(in, out); } 
			}
		}catch(Exception e){throw ExceptionUtils.convertRuntimeException(e);}
		
		
	}
	

	/**
	 * 合并路径返回URL
	 * 
	 * @param paths
	 * @return
	 */
	public static URL pathToURL(String...paths) {
		try
		{
			String path = mergePaths(paths) ;
			return new File(path).toURI().toURL() ;
		}catch(Exception e){throw ExceptionUtils.convertRuntimeException(e);}
	}
	
	
	
	
	
	 /* ------------------------------------------------------------------- */
    /** Copy Stream in to Stream out until EOF or exception.
     * @param in the input stream to read from (until EOF)
     * @param out the output stream to write to
     * @throws IOException if unable to copy streams
     */
    public static void copy(InputStream in, OutputStream out) throws IOException
    {
        copy(in,out,-1);
    }

    /* ------------------------------------------------------------------- */
    /** Copy Reader to Writer out until EOF or exception.
     * @param in the read to read from (until EOF)
     * @param out the writer to write to
     * @throws IOException if unable to copy the streams
     */
    public static void copy(Reader in, Writer out) throws IOException
    {
        copy(in,out,-1);
    }

    /* ------------------------------------------------------------------- */
    /** Copy Stream in to Stream for byteCount bytes or until EOF or exception.
     * @param in the stream to read from
     * @param out the stream to write to
     * @param byteCount the number of bytes to copy
     * @throws IOException if unable to copy the streams
     */
    public static void copy(InputStream in, OutputStream out, long byteCount) throws IOException {
        byte buffer[] = new byte[bufferSize];
        int len=bufferSize;

        if (byteCount>=0)
        {
            while (byteCount>0)
            {
                int max = byteCount<bufferSize?(int)byteCount:bufferSize;
                len=in.read(buffer,0,max);

                if (len==-1)
                    break;

                byteCount -= len;
                out.write(buffer,0,len);
            }
        }
        else
        {
            while (true)
            {
                len=in.read(buffer,0,bufferSize);
                if (len<0 )
                    break;
                out.write(buffer,0,len);
            }
        }
    }

    /* ------------------------------------------------------------------- */
    /** Copy Reader to Writer for byteCount bytes or until EOF or exception.
     * @param in the Reader to read from
     * @param out the Writer to write to
     * @param byteCount the number of bytes to copy
     * @throws IOException if unable to copy streams
     */
    public static void copy(Reader in, Writer out, long byteCount) throws IOException {
        char buffer[] = new char[bufferSize];
        int len=bufferSize;

        if (byteCount>=0)
        {
            while (byteCount>0)
            {
                if (byteCount<bufferSize)
                    len=in.read(buffer,0,(int)byteCount);
                else
                    len=in.read(buffer,0,bufferSize);

                if (len==-1)
                    break;

                byteCount -= len;
                out.write(buffer,0,len);
            }
        }
        else if (out instanceof PrintWriter)
        {
            PrintWriter pout=(PrintWriter)out;
            while (!pout.checkError())
            {
                len=in.read(buffer,0,bufferSize);
                if (len==-1)
                    break;
                out.write(buffer,0,len);
            }
        }
        else
        {
            while (true)
            {
                len=in.read(buffer,0,bufferSize);
                if (len==-1)
                    break;
                out.write(buffer,0,len);
            }
        }
    }

    /* ------------------------------------------------------------ */
    /** Copy files or directories
     * @param from the file to copy
     * @param to the destination to copy to
     * @throws IOException if unable to copy
     */
    public static void copy(File from,File to) throws IOException {
        if (from.isDirectory())
            copyDir(from,to);
        else
            copyFile(from,to);
    }

    
    public static void copyDir(File from,File to) throws IOException {
        if (to.exists())
        {
            if (!to.isDirectory())
                throw new IllegalArgumentException(to.toString());
        }
        else
            to.mkdirs();

        File[] files = from.listFiles();
        if (files!=null)
        {
            for (int i=0;i<files.length;i++)
            {
                String name = files[i].getName();
                if (".".equals(name) || "..".equals(name))
                    continue;
                copy(files[i],new File(to,name));
            }
        }
    }

    
    
    
    public static void copyFile(File from,File to) throws IOException {
        try (InputStream in=new FileInputStream(from); OutputStream out=new FileOutputStream(to))
        {
            copy(in,out);
        }
    }

    /* ------------------------------------------------------------ */
    /** Read input stream to string.
     * @param in the stream to read from (until EOF)
     * @return the String parsed from stream (default Charset)
     * @throws IOException if unable to read the stream (or handle the charset)
     */
    public static String toString(InputStream in) throws IOException {
        return toString(in,(Charset)null);
    }

    /* ------------------------------------------------------------ */
    /** Read input stream to string.
     * @param in the stream to read from (until EOF)
     * @param encoding the encoding to use (can be null to use default Charset)
     * @return the String parsed from the stream
     * @throws IOException if unable to read the stream (or handle the charset)
     */
    public static String toString(InputStream in,String encoding) throws IOException {
        return toString(in, encoding==null?null:Charset.forName(encoding));
    }

    /** Read input stream to string.
     * @param in the stream to read from (until EOF)
     * @param encoding the Charset to use (can be null to use default Charset)
     * @return the String parsed from the stream
     * @throws IOException if unable to read the stream (or handle the charset)
     */
    public static String toString(InputStream in, Charset encoding) throws IOException {
        StringWriter writer=new StringWriter();
        InputStreamReader reader = encoding==null?new InputStreamReader(in):new InputStreamReader(in,encoding);
        copy(reader,writer);
        return writer.toString();
    }

    
    
    /** Read input stream to string.
     * @param in the reader to read from (until EOF)
     * @return the String parsed from the reader
     * @throws IOException if unable to read the stream (or handle the charset)
     */
    public static String toString(Reader in) throws IOException {
        StringWriter writer=new StringWriter();
        copy(in,writer);
        return writer.toString();
    }


    /* ------------------------------------------------------------ */
    /** Delete File.
     * This delete will recursively delete directories - BE CAREFULL
     * @param file The file (or directory) to be deleted.
     * @return true if anything was deleted. (note: this does not mean that all content in a directory was deleted)
     */
    public static boolean delete(File file)
    {
        if (!file.exists())
            return false;
        if (file.isDirectory())
        {
            File[] files = file.listFiles();
            for (int i=0;files!=null && i<files.length;i++)
                delete(files[i]);
        }
        return file.delete();
    }

    
    
    
    /**
     * 
     * @param closeables
     */
    public static void close(Closeable... closeables) {
    	for(Closeable c : closeables) {
    		close(c);
    	}
    }
    
    
    
    /**
     * Closes an arbitrary closable, and logs exceptions at ignore level
     *
     * @param closeable the closeable to close
     */
    public static void close(Closeable closeable) {
        try
        {
            if (closeable != null)
                closeable.close();
        }
        catch (IOException ignore) {}
    }



    /* ------------------------------------------------------------ */
    public static byte[] readBytes(InputStream in) throws IOException {
        ByteArrayOutputStream bout = new ByteArrayOutputStream();
        copy(in,bout);
        return bout.toByteArray();
    }

    /* ------------------------------------------------------------ */
    /**
     * A gathering write utility wrapper.
     * <p>
     * This method wraps a gather write with a loop that handles the limitations of some operating systems that have a
     * limit on the number of buffers written. The method loops on the write until either all the content is written or
     * no progress is made.
     *
     * @param out
     *            The GatheringByteChannel to write to
     * @param buffers
     *            The buffers to write
     * @param offset
     *            The offset into the buffers array
     * @param length
     *            The length in buffers to write
     * @return The total bytes written
     * @throws IOException
     *             if unable write to the GatheringByteChannel
     */
    public static long write(GatheringByteChannel out, ByteBuffer[] buffers, int offset, int length) throws IOException
    {
        long total=0;
        write: while (length>0)
        {
            // Write as much as we can
            long wrote=out.write(buffers,offset,length);

            // If we can't write any more, give up
            if (wrote==0)
                break;

            // count the total
            total+=wrote;

            // Look for unwritten content
            for (int i=offset;i<buffers.length;i++)
            {
                if (buffers[i].hasRemaining())
                {
                    // loop with new offset and length;
                    length=length-(i-offset);
                    offset=i;
                    continue write;
                }
            }
            length=0;
        }

        return total;
    }

}
