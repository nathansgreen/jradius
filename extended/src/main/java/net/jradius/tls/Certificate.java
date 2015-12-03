package net.jradius.tls;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.Vector;

import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1InputStream;
import org.bouncycastle.asn1.DERObject;
import org.bouncycastle.asn1.x509.X509CertificateStructure;

/**
 * A representation for a certificate chain as used by a tls server.
 */
public class Certificate
{
    /**
     * The certificates.
     */
    protected X509CertificateStructure[] certs;

    /**
     * Parse the ServerCertificate message.
     * 
     * @param is The stream where to parse from.
     * @return A Certificate object with the certs, the server has sended.
     * @throws IOException If something goes wrong during parsing.
     */
    public static Certificate parse(InputStream is) throws IOException
    {
        X509CertificateStructure[] certs;
        int left = TlsUtils.readUint24(is);
        List<X509CertificateStructure> tmp = new Vector<X509CertificateStructure>();
        while (left > 0)
        {
            int size = TlsUtils.readUint24(is);
            left -= 3 + size;
            byte[] buf = new byte[size];
            TlsUtils.readFully(buf, is);
            ByteArrayInputStream bis = new ByteArrayInputStream(buf);
            ASN1InputStream ais = new ASN1InputStream(bis);
            DERObject o = ais.readObject();
            tmp.add(X509CertificateStructure.getInstance(o));
            if (bis.available() > 0)
            {
                throw new IllegalArgumentException(
                    "Sorry, there is garbage data left after the certificate");
            }
        }
        certs = new X509CertificateStructure[tmp.size()];
        for (int i = 0; i < tmp.size(); i++)
        {
            certs[i] = tmp.get(i);
        }
        return new Certificate(certs);
    }

    /**
     * Encodes version of the ClientCertificate message
     * 
     * @param os stream to write the message to
     * @throws IOException If something goes wrong
     */
    protected void encode(OutputStream os) throws IOException
    {
        List<byte[]> encCerts = new Vector<byte[]>();
        int totalSize = 0;
        for (int i = 0; i < this.certs.length; ++i)
        {
            byte[] encCert = certs[i].getEncoded(ASN1Encodable.DER);
            encCerts.add(encCert);
            totalSize += encCert.length + 3;
        }

        TlsUtils.writeUint24(totalSize + 3, os);
        TlsUtils.writeUint24(totalSize, os);

        for (int i = 0; i < encCerts.size(); ++i)
        {
            byte[] encCert = encCerts.get(i);
            TlsUtils.writeOpaque24(encCert, os);
        }
    }

    /**
     * Private constructor from a cert array.
     * 
     * @param certs The certs the chain should contain.
     */
    public Certificate(X509CertificateStructure[] certs)
    {
        this.certs = certs;
    }

    /**
     * @return An array which contains the certs, this chain contains.
     */
    public X509CertificateStructure[] getCerts()
    {
        X509CertificateStructure[] result = new X509CertificateStructure[certs.length];
        System.arraycopy(certs, 0, result, 0, certs.length);
        return result;
    }
}
