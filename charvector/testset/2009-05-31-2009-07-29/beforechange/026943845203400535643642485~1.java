/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.jackrabbit.core.value;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.ByteArrayOutputStream;
import java.io.UnsupportedEncodingException;

import javax.jcr.RepositoryException;
import javax.jcr.Binary;

import org.apache.commons.io.IOUtils;
import org.apache.jackrabbit.core.data.DataIdentifier;

/**
 * Represents binary data which is backed by a resource or byte[].
 * Unlike <code>BinaryValue</code> it has no state, i.e.
 * the <code>getStream()</code> method always returns a fresh
 * <code>InputStream</code> instance.
 * <p/>
 * <b>Important Note:</b><p/>
 * This interface is for Jackrabbit-internal use only. Applications should
 * use <code>javax.jcr.ValueFactory</code> to create binary values.
 */
public abstract class BLOBFileValue implements Binary {

    /**
     * Returns a String representation of this value.
     *
     * @return String representation of this value.
     * @throws RepositoryException
     */
    public String getString() throws RepositoryException {
        // TODO: review again. currently the getString method of the JCR Value is delegated to the QValue.
        InputStream stream = getStream();
        try {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            byte[] buffer = new byte[8192];
            int read;
            while ((read = stream.read(buffer)) > 0) {
                out.write(buffer, 0, read);
            }
            byte[] data = out.toByteArray();
            return new String(data, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new RepositoryException("UTF-8 not supported on this platform", e);
        } catch (IOException e) {
            throw new RepositoryException("conversion from stream to string failed", e);
        } finally {
            try {
                if (stream != null) {
                    stream.close();
                }
            } catch (IOException e) {
                // ignore
            }
        }
    }

    /**
     * Returns the length of this <code>BLOBFileValue</code>.
     *
     * @return The length, in bytes, of this <code>BLOBFileValue</code>,
     *         or -1L if the length can't be determined.
     * @throws IOException
     */
    public abstract long getLength();

    /**
     * Frees temporarily allocated resources such as temporary file, buffer, etc.
     * If this <code>BLOBFileValue</code> is backed by a persistent resource
     * calling this method will have no effect.
     *
     * @see #delete(boolean)
     */
    public abstract void discard();

    /**
     * Deletes the persistent resource backing this <code>BLOBFileValue</code>.
     *
     * @param pruneEmptyParentDirs if <code>true</code>, empty parent directories
     *                             will automatically be deleted
     */
    public abstract void delete(boolean pruneEmptyParentDirs);

    /**
     * Checks if this object is immutable.
     * Immutable objects can not change and can safely copied.
     *
     * @return true if the object is immutable
     */
    public abstract boolean isImmutable();

    /**
     * {@inheritDoc}
     */
    public abstract boolean equals(Object obj);

    /**
     * {@inheritDoc}
     */
    public abstract String toString();

    /*
     * Spools the contents of this <code>BLOBFileValue</code> to the given
     * output stream.
     *
     * @param out output stream
     * @throws RepositoryException if the input stream for this
     *                             <code>BLOBFileValue</code> could not be obtained
     * @throws IOException         if an error occurs while while spooling
     */
    public void spool(OutputStream out) throws RepositoryException, IOException {
        InputStream in = getStream();
        try {
            IOUtils.copy(in, out);
        } finally {
            IOUtils.closeQuietly(in);
        }
    }

    /**
     * {@inheritDoc}
     */
    public abstract int hashCode();

    /**
     * Check if the value is small (contains a low number of bytes) and should
     * be stored inline.
     *
     * @return true if the value is small
     */
    public abstract boolean isSmall();
    
    /**
     * Get the data identifier if one is available.
     * 
     * @return the data identifier or null
     */
    public DataIdentifier getDataIdentifier() {
        return null;
    }

    //-----------------------------------------------------< javax.jcr.Binary >
    /**
     * {@inheritDoc}
     */
    public abstract InputStream getStream() throws RepositoryException;

    /**
     * {@inheritDoc}
     */
    public int read(byte[] b, long position) throws IOException, RepositoryException {
        InputStream in = getStream();
        try {
            in.skip(position);
            return in.read(b);
        } finally {
            in.close();
        }
    }

    /**
     * {@inheritDoc}
     */
    public long getSize() throws RepositoryException {
        return getLength();
    }

    public void dispose() {
        discard();
    }

}