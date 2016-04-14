/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package qut.edu.au.analysis;

import com.eviware.soapui.SoapUI;
import com.eviware.soapui.settings.WsdlSettings;
import com.eviware.soapui.support.StringUtils;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.xml.namespace.QName;
import org.apache.xmlbeans.GDate;
import org.apache.xmlbeans.GDateBuilder;
import org.apache.xmlbeans.GDuration;
import org.apache.xmlbeans.GDurationBuilder;
import org.apache.xmlbeans.SchemaLocalElement;
import org.apache.xmlbeans.SchemaParticle;
import org.apache.xmlbeans.SchemaProperty;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.SimpleValue;
import org.apache.xmlbeans.XmlAnySimpleType;
import org.apache.xmlbeans.XmlCursor;
import org.apache.xmlbeans.XmlDate;
import org.apache.xmlbeans.XmlDateTime;
import org.apache.xmlbeans.XmlDecimal;
import org.apache.xmlbeans.XmlDuration;
import org.apache.xmlbeans.XmlGDay;
import org.apache.xmlbeans.XmlGMonth;
import org.apache.xmlbeans.XmlGMonthDay;
import org.apache.xmlbeans.XmlGYear;
import org.apache.xmlbeans.XmlGYearMonth;
import org.apache.xmlbeans.XmlInteger;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.XmlTime;
import org.apache.xmlbeans.impl.util.Base64;
import org.apache.xmlbeans.impl.util.HexBin;
import org.apache.xmlbeans.soap.SOAPArrayType;
import org.apache.xmlbeans.soap.SchemaWSDLArrayType;
import qut.edu.au.services.Parameter;
import qut.edu.au.Utility;

/**
 *
 * @author sih
 */
public class SampleXmlUtility {

    private boolean _soapEnc;
    // private static DefaultMutableTreeNode root = new DefaultMutableTreeNode("Root");

    public SampleXmlUtility(boolean _soapEnc) {
        this.counter = 0;
        this._soapEnc = _soapEnc;
        //excludedTypes.addAll(SchemaUtils.getExcludedTypes());
    }

    public boolean isSoapEnc() {
        return _soapEnc;
    }
    private boolean _exampleContent = false;
    private boolean _typeComment = true;
    private boolean _skipComments;
    private Map<QName, String[]> multiValues = null;

    public void setMultiValues(Map<QName, String[]> multiValues) {
        this.multiValues = multiValues;
    }

    public void setIgnoreOptional(boolean ignoreOptional) {
        this.ignoreOptional = ignoreOptional;
    }
    private boolean ignoreOptional;
    private int counter;

    private Set<QName> excludedTypes = new HashSet<QName>();
    private ArrayList<SchemaType> _typeStack = new ArrayList<SchemaType>();
    Random _picker = new Random(1);
    private static final QName HREF = new QName("href");
    private static final QName ID = new QName("id");
    public static final QName XSI_TYPE = new QName("http://www.w3.org/2001/XMLSchema-instance", "type");
    public static final QName ENC_ARRAYTYPE = new QName("http://schemas.xmlsoap.org/soap/encoding/", "arrayType");
    private static final QName ENC_OFFSET = new QName("http://schemas.xmlsoap.org/soap/encoding/", "offset");

    public static final Set<QName> SKIPPED_SOAP_ATTRS = new HashSet<QName>(Arrays.asList(new QName[]{HREF, ID, ENC_OFFSET}));

    public static final String[] WORDS = new String[]{"ipsa", "iovis", "rapidum", "iaculata", "e", "nubibus", "ignem",
        "disiecitque", "rates", "evertitque", "aequora", "ventis", "illum", "exspirantem", "transfixo", "pectore",
        "flammas", "turbine", "corripuit", "scopuloque", "infixit", "acuto", "ast", "ego", "quae", "divum", "incedo",
        "regina", "iovisque", "et", "soror", "et", "coniunx", "una", "cum", "gente", "tot", "annos", "bella", "gero",
        "et", "quisquam", "numen", "iunonis", "adorat", "praeterea", "aut", "supplex", "aris", "imponet", "honorem",
        "talia", "flammato", "secum", "dea", "corde", "volutans", "nimborum", "in", "patriam", "loca", "feta",
        "furentibus", "austris", "aeoliam", "venit", "hic", "vasto", "rex", "aeolus", "antro", "luctantis", "ventos",
        "tempestatesque", "sonoras", "imperio", "premit", "ac", "vinclis", "et", "carcere", "frenat", "illi",
        "indignantes", "magno", "cum", "murmure", "montis", "circum", "claustra", "fremunt", "celsa", "sedet",
        "aeolus", "arce", "sceptra", "tenens", "mollitque", "animos", "et", "temperat", "iras", "ni", "faciat",
        "maria", "ac", "terras", "caelumque", "profundum", "quippe", "ferant", "rapidi", "secum", "verrantque", "per",
        "auras", "sed", "pater", "omnipotens", "speluncis", "abdidit", "atris", "hoc", "metuens", "molemque", "et",
        "montis", "insuper", "altos", "imposuit", "regemque", "dedit", "qui", "foedere", "certo", "et", "premere",
        "et", "laxas", "sciret", "dare", "iussus", "habenas",};

    private static final String[] DNS1 = new String[]{"corp", "your", "my", "sample", "company", "test", "any"};
    private static final String[] DNS2 = new String[]{"com", "org", "com", "gov", "org", "com", "org", "com", "edu"};

    private static final String formatQName(XmlCursor xmlc, QName qName) {
        XmlCursor parent = xmlc.newCursor();
        parent.toParent();
        String prefix = parent.prefixForNamespace(qName.getNamespaceURI());
        parent.dispose();
        String name;
        if (prefix == null || prefix.length() == 0) {
            name = qName.getLocalPart();
        } else {
            name = prefix + ":" + qName.getLocalPart();
        }
        return name;
    }

    private int pick(int n) {
        return _picker.nextInt(n);
    }

    private String pick(String[] a) {
        return a[pick(a.length)];
    }

    private String pick(String[] a, int count) {
        if (count <= 0) {
            count = 1;
        }
        // return "";
        int i = pick(a.length);
        StringBuffer sb = new StringBuffer(a[i]);
        while (count-- > 0) {
            i += 1;
            if (i >= a.length) {
                i = 0;
            }
            sb.append(' ');
            sb.append(a[i]);
        }
        return sb.toString();
    }

    private String formatDuration(SchemaType sType) {
        XmlDuration d = (XmlDuration) sType.getFacet(SchemaType.FACET_MIN_INCLUSIVE);
        GDuration minInclusive = null;
        if (d != null) {
            minInclusive = d.getGDurationValue();
        }
        d = (XmlDuration) sType.getFacet(SchemaType.FACET_MAX_INCLUSIVE);
        GDuration maxInclusive = null;
        if (d != null) {
            maxInclusive = d.getGDurationValue();
        }
        d = (XmlDuration) sType.getFacet(SchemaType.FACET_MIN_EXCLUSIVE);
        GDuration minExclusive = null;
        if (d != null) {
            minExclusive = d.getGDurationValue();
        }
        d = (XmlDuration) sType.getFacet(SchemaType.FACET_MAX_EXCLUSIVE);
        GDuration maxExclusive = null;
        if (d != null) {
            maxExclusive = d.getGDurationValue();
        }
        GDurationBuilder gdurb = new GDurationBuilder();
        @SuppressWarnings("unused")
        BigInteger min, max;
        gdurb.setSecond(pick(800000));
        gdurb.setMonth(pick(20));
        // Years
        // Months
        // Days
        // Hours
        // Minutes
        // Seconds
        // Fractions
        if (minInclusive != null) {
            if (gdurb.getYear() < minInclusive.getYear()) {
                gdurb.setYear(minInclusive.getYear());
            }
            if (gdurb.getMonth() < minInclusive.getMonth()) {
                gdurb.setMonth(minInclusive.getMonth());
            }
            if (gdurb.getDay() < minInclusive.getDay()) {
                gdurb.setDay(minInclusive.getDay());
            }
            if (gdurb.getHour() < minInclusive.getHour()) {
                gdurb.setHour(minInclusive.getHour());
            }
            if (gdurb.getMinute() < minInclusive.getMinute()) {
                gdurb.setMinute(minInclusive.getMinute());
            }
            if (gdurb.getSecond() < minInclusive.getSecond()) {
                gdurb.setSecond(minInclusive.getSecond());
            }
            if (gdurb.getFraction().compareTo(minInclusive.getFraction()) < 0) {
                gdurb.setFraction(minInclusive.getFraction());
            }
        }
        if (maxInclusive != null) {
            if (gdurb.getYear() > maxInclusive.getYear()) {
                gdurb.setYear(maxInclusive.getYear());
            }
            if (gdurb.getMonth() > maxInclusive.getMonth()) {
                gdurb.setMonth(maxInclusive.getMonth());
            }
            if (gdurb.getDay() > maxInclusive.getDay()) {
                gdurb.setDay(maxInclusive.getDay());
            }
            if (gdurb.getHour() > maxInclusive.getHour()) {
                gdurb.setHour(maxInclusive.getHour());
            }
            if (gdurb.getMinute() > maxInclusive.getMinute()) {
                gdurb.setMinute(maxInclusive.getMinute());
            }
            if (gdurb.getSecond() > maxInclusive.getSecond()) {
                gdurb.setSecond(maxInclusive.getSecond());
            }
            if (gdurb.getFraction().compareTo(maxInclusive.getFraction()) > 0) {
                gdurb.setFraction(maxInclusive.getFraction());
            }
        }
        if (minExclusive != null) {
            if (gdurb.getYear() <= minExclusive.getYear()) {
                gdurb.setYear(minExclusive.getYear() + 1);
            }
            if (gdurb.getMonth() <= minExclusive.getMonth()) {
                gdurb.setMonth(minExclusive.getMonth() + 1);
            }
            if (gdurb.getDay() <= minExclusive.getDay()) {
                gdurb.setDay(minExclusive.getDay() + 1);
            }
            if (gdurb.getHour() <= minExclusive.getHour()) {
                gdurb.setHour(minExclusive.getHour() + 1);
            }
            if (gdurb.getMinute() <= minExclusive.getMinute()) {
                gdurb.setMinute(minExclusive.getMinute() + 1);
            }
            if (gdurb.getSecond() <= minExclusive.getSecond()) {
                gdurb.setSecond(minExclusive.getSecond() + 1);
            }
            if (gdurb.getFraction().compareTo(minExclusive.getFraction()) <= 0) {
                gdurb.setFraction(minExclusive.getFraction().add(new BigDecimal("0.001")));
            }
        }
        if (maxExclusive != null) {
            if (gdurb.getYear() > maxExclusive.getYear()) {
                gdurb.setYear(maxExclusive.getYear());
            }
            if (gdurb.getMonth() > maxExclusive.getMonth()) {
                gdurb.setMonth(maxExclusive.getMonth());
            }
            if (gdurb.getDay() > maxExclusive.getDay()) {
                gdurb.setDay(maxExclusive.getDay());
            }
            if (gdurb.getHour() > maxExclusive.getHour()) {
                gdurb.setHour(maxExclusive.getHour());
            }
            if (gdurb.getMinute() > maxExclusive.getMinute()) {
                gdurb.setMinute(maxExclusive.getMinute());
            }
            if (gdurb.getSecond() > maxExclusive.getSecond()) {
                gdurb.setSecond(maxExclusive.getSecond());
            }
            if (gdurb.getFraction().compareTo(maxExclusive.getFraction()) > 0) {
                gdurb.setFraction(maxExclusive.getFraction());
            }
        }
        gdurb.normalize();
        return gdurb.toString();
    }

    private int pickLength(SchemaType sType) {
        XmlInteger length = (XmlInteger) sType.getFacet(SchemaType.FACET_LENGTH);
        if (length != null) {
            return length.getBigIntegerValue().intValue();
        }
        XmlInteger min = (XmlInteger) sType.getFacet(SchemaType.FACET_MIN_LENGTH);
        XmlInteger max = (XmlInteger) sType.getFacet(SchemaType.FACET_MAX_LENGTH);
        int minInt, maxInt;
        if (min == null) {
            minInt = 0;
        } else {
            minInt = min.getBigIntegerValue().intValue();
        }
        if (max == null) {
            maxInt = Integer.MAX_VALUE;
        } else {
            maxInt = max.getBigIntegerValue().intValue();
        }
        // We try to keep the length of the array within reasonable limits,
        // at least 1 item and at most 3 if possible
        if (minInt == 0 && maxInt >= 1) {
            minInt = 1;
        }
        if (maxInt > minInt + 2) {
            maxInt = minInt + 2;
        }
        if (maxInt < minInt) {
            maxInt = minInt;
        }
        return minInt + pick(maxInt - minInt);
    }

    /**
     * Formats a given string to the required length, using the following
     * operations: - append the source string to itself as necessary to pass the
     * minLength; - truncate the result of previous step, if necessary, to keep
     * it within minLength.
     */
    private String formatToLength(String s, SchemaType sType) {
        String result = s;
        try {
            SimpleValue min = (SimpleValue) sType.getFacet(SchemaType.FACET_LENGTH);
            if (min == null) {
                min = (SimpleValue) sType.getFacet(SchemaType.FACET_MIN_LENGTH);
            }
            if (min != null) {
                int len = min.getIntValue();
                while (result.length() < len) {
                    result = result + result;
                }
            }
            SimpleValue max = (SimpleValue) sType.getFacet(SchemaType.FACET_LENGTH);
            if (max == null) {
                max = (SimpleValue) sType.getFacet(SchemaType.FACET_MAX_LENGTH);
            }
            if (max != null) {
                int len = max.getIntValue();
                if (result.length() > len) {
                    result = result.substring(0, len);
                }
            }
        } catch (Exception e) // intValue can be out of range
        {
        }
        return result;
    }

    private SchemaType closestBuiltin(SchemaType sType) {
        while (!sType.isBuiltinType()) {
            sType = sType.getBaseType();
        }
        return sType;
    }

    private String formatDecimal(String start, SchemaType sType) {
        BigDecimal result = new BigDecimal(start);
        XmlDecimal xmlD;
        xmlD = (XmlDecimal) sType.getFacet(SchemaType.FACET_MIN_INCLUSIVE);
        BigDecimal min = xmlD != null ? xmlD.getBigDecimalValue() : null;
        xmlD = (XmlDecimal) sType.getFacet(SchemaType.FACET_MAX_INCLUSIVE);
        BigDecimal max = xmlD != null ? xmlD.getBigDecimalValue() : null;
        boolean minInclusive = true, maxInclusive = true;
        xmlD = (XmlDecimal) sType.getFacet(SchemaType.FACET_MIN_EXCLUSIVE);
        if (xmlD != null) {
            BigDecimal minExcl = xmlD.getBigDecimalValue();
            if (min == null || min.compareTo(minExcl) < 0) {
                min = minExcl;
                minInclusive = false;
            }
        }
        xmlD = (XmlDecimal) sType.getFacet(SchemaType.FACET_MAX_EXCLUSIVE);
        if (xmlD != null) {
            BigDecimal maxExcl = xmlD.getBigDecimalValue();
            if (max == null || max.compareTo(maxExcl) > 0) {
                max = maxExcl;
                maxInclusive = false;
            }
        }
        xmlD = (XmlDecimal) sType.getFacet(SchemaType.FACET_TOTAL_DIGITS);
        int totalDigits = -1;
        if (xmlD != null) {
            totalDigits = xmlD.getBigDecimalValue().intValue();
            StringBuffer sb = new StringBuffer(totalDigits);
            for (int i = 0; i < totalDigits; i++) {
                sb.append('9');
            }
            BigDecimal digitsLimit = new BigDecimal(sb.toString());
            if (max != null && max.compareTo(digitsLimit) > 0) {
                max = digitsLimit;
                maxInclusive = true;
            }
            digitsLimit = digitsLimit.negate();
            if (min != null && min.compareTo(digitsLimit) < 0) {
                min = digitsLimit;
                minInclusive = true;
            }
        }
        int sigMin = min == null ? 1 : result.compareTo(min);
        int sigMax = max == null ? -1 : result.compareTo(max);
        boolean minOk = sigMin > 0 || sigMin == 0 && minInclusive;
        boolean maxOk = sigMax < 0 || sigMax == 0 && maxInclusive;
        // Compute the minimum increment
        xmlD = (XmlDecimal) sType.getFacet(SchemaType.FACET_FRACTION_DIGITS);
        int fractionDigits = -1;
        BigDecimal increment;
        if (xmlD == null) {
            increment = new BigDecimal(1);
        } else {
            fractionDigits = xmlD.getBigDecimalValue().intValue();
            if (fractionDigits > 0) {
                StringBuffer sb = new StringBuffer("0.");
                for (int i = 1; i < fractionDigits; i++) {
                    sb.append('0');
                }
                sb.append('1');
                increment = new BigDecimal(sb.toString());
            } else {
                increment = new BigDecimal(1);
            }
        }
        if (minOk && maxOk) {
            // OK
        } else if (minOk && !maxOk) {
            // TOO BIG
            if (maxInclusive) {
                result = max;
            } else {
                result = max.subtract(increment);
            }
        } else if (!minOk && maxOk) {
            // TOO SMALL
            if (minInclusive) {
                result = min;
            } else {
                result = min.add(increment);
            }
        } else {
            // MIN > MAX!!
        }
        // We have the number
        // Adjust the scale according to the totalDigits and fractionDigits
        int digits = 0;
        BigDecimal ONE = new BigDecimal(BigInteger.ONE);
        for (BigDecimal n = result; n.abs().compareTo(ONE) >= 0; digits++) {
            n = n.movePointLeft(1);
        }
        if (fractionDigits > 0) {
            if (totalDigits >= 0) {
                result.setScale(Math.max(fractionDigits, totalDigits - digits));
            } else {
                result.setScale(fractionDigits);
            }
        } else if (fractionDigits == 0) {
            result.setScale(0);
        }
        return result.toString();
    }

    private String sampleDataForSimpleType(SchemaType sType) {
        // swaRef
        if (sType.getName() != null) {
            if (sType.getName().equals(new QName("http://ws-i.org/profiles/basic/1.1/xsd", "swaRef"))) {
                return "cid:" + (long) (System.currentTimeMillis() * Math.random());
            }
            // xmime base64
            if (sType.getName().equals(new QName("http://www.w3.org/2005/05/xmlmime", "base64Binary"))) {
                return "cid:" + (long) (System.currentTimeMillis() * Math.random());
            }
            // xmime hexBinary
            if (sType.getName().equals(new QName("http://www.w3.org/2005/05/xmlmime", "hexBinary"))) {
                return "cid:" + (long) (System.currentTimeMillis() * Math.random());
            }
        }
        SchemaType primitiveType = sType.getPrimitiveType();
        if (primitiveType != null
                && (primitiveType.getBuiltinTypeCode() == SchemaType.BTC_BASE_64_BINARY || primitiveType
                .getBuiltinTypeCode() == SchemaType.BTC_HEX_BINARY)) {
            return "cid:" + (long) (System.currentTimeMillis() * Math.random());
        }
        // if( sType != null )
        if (!_exampleContent) {
            return "?";
        }
        if (XmlObject.type.equals(sType)) {
            return "anyType";
        }
        if (XmlAnySimpleType.type.equals(sType)) {
            return "anySimpleType";
        }
        if (sType.getSimpleVariety() == SchemaType.LIST) {
            SchemaType itemType = sType.getListItemType();
            StringBuffer sb = new StringBuffer();
            int length = pickLength(sType);
            if (length > 0) {
                sb.append(sampleDataForSimpleType(itemType));
            }
            for (int i = 1; i < length; i += 1) {
                sb.append(' ');
                sb.append(sampleDataForSimpleType(itemType));
            }
            return sb.toString();
        }
        if (sType.getSimpleVariety() == SchemaType.UNION) {
            SchemaType[] possibleTypes = sType.getUnionConstituentTypes();
            if (possibleTypes.length == 0) {
                return "";
            }
            return sampleDataForSimpleType(possibleTypes[pick(possibleTypes.length)]);
        }
        XmlAnySimpleType[] enumValues = sType.getEnumerationValues();
        if (enumValues != null && enumValues.length > 0) {
            return enumValues[pick(enumValues.length)].getStringValue();
        }
        switch (primitiveType.getBuiltinTypeCode()) {
            default:
            case SchemaType.BTC_NOT_BUILTIN:
                return "";
            case SchemaType.BTC_ANY_TYPE:
            case SchemaType.BTC_ANY_SIMPLE:
                return "anything";
            case SchemaType.BTC_BOOLEAN:
                return pick(2) == 0 ? "true" : "false";
            case SchemaType.BTC_BASE_64_BINARY: {
                String result = null;
                try {
                    result = new String(Base64.encode(formatToLength(pick(WORDS), sType).getBytes("utf-8")));
                } catch (Exception e) {
                    System.out.print(e.getMessage());
                }
                return result;
            }
            case SchemaType.BTC_HEX_BINARY:
                return HexBin.encode(formatToLength(pick(WORDS), sType));
            case SchemaType.BTC_ANY_URI:
                return formatToLength("http://www." + pick(DNS1) + "." + pick(DNS2) + "/" + pick(WORDS) + "/"
                        + pick(WORDS), sType);
            case SchemaType.BTC_QNAME:
                return formatToLength("qname", sType);
            case SchemaType.BTC_NOTATION:
                return formatToLength("notation", sType);
            case SchemaType.BTC_FLOAT:
                return "1.5E2";
            case SchemaType.BTC_DOUBLE:
                return "1.051732E7";
            case SchemaType.BTC_DECIMAL:
                switch (closestBuiltin(sType).getBuiltinTypeCode()) {
                    case SchemaType.BTC_SHORT:
                        return formatDecimal("1", sType);
                    case SchemaType.BTC_UNSIGNED_SHORT:
                        return formatDecimal("5", sType);
                    case SchemaType.BTC_BYTE:
                        return formatDecimal("2", sType);
                    case SchemaType.BTC_UNSIGNED_BYTE:
                        return formatDecimal("6", sType);
                    case SchemaType.BTC_INT:
                        return formatDecimal("3", sType);
                    case SchemaType.BTC_UNSIGNED_INT:
                        return formatDecimal("7", sType);
                    case SchemaType.BTC_LONG:
                        return formatDecimal("10", sType);
                    case SchemaType.BTC_UNSIGNED_LONG:
                        return formatDecimal("11", sType);
                    case SchemaType.BTC_INTEGER:
                        return formatDecimal("100", sType);
                    case SchemaType.BTC_NON_POSITIVE_INTEGER:
                        return formatDecimal("-200", sType);
                    case SchemaType.BTC_NEGATIVE_INTEGER:
                        return formatDecimal("-201", sType);
                    case SchemaType.BTC_NON_NEGATIVE_INTEGER:
                        return formatDecimal("200", sType);
                    case SchemaType.BTC_POSITIVE_INTEGER:
                        return formatDecimal("201", sType);
                    default:
                    case SchemaType.BTC_DECIMAL:
                        return formatDecimal("1000.00", sType);
                }
            case SchemaType.BTC_STRING: {
                String result;
                switch (closestBuiltin(sType).getBuiltinTypeCode()) {
                    case SchemaType.BTC_STRING:
                    case SchemaType.BTC_NORMALIZED_STRING:
                        result = pick(WORDS, _picker.nextInt(3));
                        break;
                    case SchemaType.BTC_TOKEN:
                        result = pick(WORDS, _picker.nextInt(3));
                        break;
                    default:
                        result = pick(WORDS, _picker.nextInt(3));
                        break;
                }
                return formatToLength(result, sType);
            }
            case SchemaType.BTC_DURATION:
                return formatDuration(sType);
            case SchemaType.BTC_DATE_TIME:
            case SchemaType.BTC_TIME:
            case SchemaType.BTC_DATE:
            case SchemaType.BTC_G_YEAR_MONTH:
            case SchemaType.BTC_G_YEAR:
            case SchemaType.BTC_G_MONTH_DAY:
            case SchemaType.BTC_G_DAY:
            case SchemaType.BTC_G_MONTH:
                return formatDate(sType);
        }
    }

    private String formatDate(SchemaType sType) {
        GDateBuilder gdateb = new GDateBuilder(new Date(1000L * pick(365 * 24 * 60 * 60) + (30L + pick(20)) * 365
                * 24 * 60 * 60 * 1000));
        GDate min = null, max = null;
// Find the min and the max according to the type
        switch (sType.getPrimitiveType().getBuiltinTypeCode()) {
            case SchemaType.BTC_DATE_TIME: {
                XmlDateTime x = (XmlDateTime) sType.getFacet(SchemaType.FACET_MIN_INCLUSIVE);
                if (x != null) {
                    min = x.getGDateValue();
                }
                x = (XmlDateTime) sType.getFacet(SchemaType.FACET_MIN_EXCLUSIVE);
                if (x != null) {
                    if (min == null || min.compareToGDate(x.getGDateValue()) <= 0) {
                        min = x.getGDateValue();
                    }
                }
                x = (XmlDateTime) sType.getFacet(SchemaType.FACET_MAX_INCLUSIVE);
                if (x != null) {
                    max = x.getGDateValue();
                }
                x = (XmlDateTime) sType.getFacet(SchemaType.FACET_MAX_EXCLUSIVE);
                if (x != null) {
                    if (max == null || max.compareToGDate(x.getGDateValue()) >= 0) {
                        max = x.getGDateValue();
                    }
                }
                break;
            }
            case SchemaType.BTC_TIME: {
                XmlTime x = (XmlTime) sType.getFacet(SchemaType.FACET_MIN_INCLUSIVE);
                if (x != null) {
                    min = x.getGDateValue();
                }
                x = (XmlTime) sType.getFacet(SchemaType.FACET_MIN_EXCLUSIVE);
                if (x != null) {
                    if (min == null || min.compareToGDate(x.getGDateValue()) <= 0) {
                        min = x.getGDateValue();
                    }
                }
                x = (XmlTime) sType.getFacet(SchemaType.FACET_MAX_INCLUSIVE);
                if (x != null) {
                    max = x.getGDateValue();
                }
                x = (XmlTime) sType.getFacet(SchemaType.FACET_MAX_EXCLUSIVE);
                if (x != null) {
                    if (max == null || max.compareToGDate(x.getGDateValue()) >= 0) {
                        max = x.getGDateValue();
                    }
                }
                break;
            }
            case SchemaType.BTC_DATE: {
                XmlDate x = (XmlDate) sType.getFacet(SchemaType.FACET_MIN_INCLUSIVE);
                if (x != null) {
                    min = x.getGDateValue();
                }
                x = (XmlDate) sType.getFacet(SchemaType.FACET_MIN_EXCLUSIVE);
                if (x != null) {
                    if (min == null || min.compareToGDate(x.getGDateValue()) <= 0) {
                        min = x.getGDateValue();
                    }
                }
                x = (XmlDate) sType.getFacet(SchemaType.FACET_MAX_INCLUSIVE);
                if (x != null) {
                    max = x.getGDateValue();
                }
                x = (XmlDate) sType.getFacet(SchemaType.FACET_MAX_EXCLUSIVE);
                if (x != null) {
                    if (max == null || max.compareToGDate(x.getGDateValue()) >= 0) {
                        max = x.getGDateValue();
                    }
                }
                break;
            }
            case SchemaType.BTC_G_YEAR_MONTH: {
                XmlGYearMonth x = (XmlGYearMonth) sType.getFacet(SchemaType.FACET_MIN_INCLUSIVE);
                if (x != null) {
                    min = x.getGDateValue();
                }
                x = (XmlGYearMonth) sType.getFacet(SchemaType.FACET_MIN_EXCLUSIVE);
                if (x != null) {
                    if (min == null || min.compareToGDate(x.getGDateValue()) <= 0) {
                        min = x.getGDateValue();
                    }
                }
                x = (XmlGYearMonth) sType.getFacet(SchemaType.FACET_MAX_INCLUSIVE);
                if (x != null) {
                    max = x.getGDateValue();
                }
                x = (XmlGYearMonth) sType.getFacet(SchemaType.FACET_MAX_EXCLUSIVE);
                if (x != null) {
                    if (max == null || max.compareToGDate(x.getGDateValue()) >= 0) {
                        max = x.getGDateValue();
                    }
                }
                break;
            }
            case SchemaType.BTC_G_YEAR: {
                XmlGYear x = (XmlGYear) sType.getFacet(SchemaType.FACET_MIN_INCLUSIVE);
                if (x != null) {
                    min = x.getGDateValue();
                }
                x = (XmlGYear) sType.getFacet(SchemaType.FACET_MIN_EXCLUSIVE);
                if (x != null) {
                    if (min == null || min.compareToGDate(x.getGDateValue()) <= 0) {
                        min = x.getGDateValue();
                    }
                }
                x = (XmlGYear) sType.getFacet(SchemaType.FACET_MAX_INCLUSIVE);
                if (x != null) {
                    max = x.getGDateValue();
                }
                x = (XmlGYear) sType.getFacet(SchemaType.FACET_MAX_EXCLUSIVE);
                if (x != null) {
                    if (max == null || max.compareToGDate(x.getGDateValue()) >= 0) {
                        max = x.getGDateValue();
                    }
                }
                break;
            }
            case SchemaType.BTC_G_MONTH_DAY: {
                XmlGMonthDay x = (XmlGMonthDay) sType.getFacet(SchemaType.FACET_MIN_INCLUSIVE);
                if (x != null) {
                    min = x.getGDateValue();
                }
                x = (XmlGMonthDay) sType.getFacet(SchemaType.FACET_MIN_EXCLUSIVE);
                if (x != null) {
                    if (min == null || min.compareToGDate(x.getGDateValue()) <= 0) {
                        min = x.getGDateValue();
                    }
                }
                x = (XmlGMonthDay) sType.getFacet(SchemaType.FACET_MAX_INCLUSIVE);
                if (x != null) {
                    max = x.getGDateValue();
                }
                x = (XmlGMonthDay) sType.getFacet(SchemaType.FACET_MAX_EXCLUSIVE);
                if (x != null) {
                    if (max == null || max.compareToGDate(x.getGDateValue()) >= 0) {
                        max = x.getGDateValue();
                    }
                }
                break;
            }
            case SchemaType.BTC_G_DAY: {
                XmlGDay x = (XmlGDay) sType.getFacet(SchemaType.FACET_MIN_INCLUSIVE);
                if (x != null) {
                    min = x.getGDateValue();
                }
                x = (XmlGDay) sType.getFacet(SchemaType.FACET_MIN_EXCLUSIVE);
                if (x != null) {
                    if (min == null || min.compareToGDate(x.getGDateValue()) <= 0) {
                        min = x.getGDateValue();
                    }
                }
                x = (XmlGDay) sType.getFacet(SchemaType.FACET_MAX_INCLUSIVE);
                if (x != null) {
                    max = x.getGDateValue();
                }
                x = (XmlGDay) sType.getFacet(SchemaType.FACET_MAX_EXCLUSIVE);
                if (x != null) {
                    if (max == null || max.compareToGDate(x.getGDateValue()) >= 0) {
                        max = x.getGDateValue();
                    }
                }
                break;
            }
            case SchemaType.BTC_G_MONTH: {
                XmlGMonth x = (XmlGMonth) sType.getFacet(SchemaType.FACET_MIN_INCLUSIVE);
                if (x != null) {
                    min = x.getGDateValue();
                }
                x = (XmlGMonth) sType.getFacet(SchemaType.FACET_MIN_EXCLUSIVE);
                if (x != null) {
                    if (min == null || min.compareToGDate(x.getGDateValue()) <= 0) {
                        min = x.getGDateValue();
                    }
                }
                x = (XmlGMonth) sType.getFacet(SchemaType.FACET_MAX_INCLUSIVE);
                if (x != null) {
                    max = x.getGDateValue();
                }
                x = (XmlGMonth) sType.getFacet(SchemaType.FACET_MAX_EXCLUSIVE);
                if (x != null) {
                    if (max == null || max.compareToGDate(x.getGDateValue()) >= 0) {
                        max = x.getGDateValue();
                    }
                }
                break;
            }
        }
        if (min != null && max == null) {
            if (min.compareToGDate(gdateb) >= 0) {
// Reset the date to min + (1-8) hours
                Calendar c = gdateb.getCalendar();
                c.add(Calendar.HOUR_OF_DAY, pick(8));
                gdateb = new GDateBuilder(c);
            }
        } else if (min == null && max != null) {
            if (max.compareToGDate(gdateb) <= 0) {
// Reset the date to max - (1-8) hours
                Calendar c = gdateb.getCalendar();
                c.add(Calendar.HOUR_OF_DAY, 0 - pick(8));
                gdateb = new GDateBuilder(c);
            }
        } else if (min != null && max != null) {
            if (min.compareToGDate(gdateb) >= 0 || max.compareToGDate(gdateb) <= 0) {
// Find a date between the two
                Calendar c = min.getCalendar();
                Calendar cmax = max.getCalendar();
                c.add(Calendar.HOUR_OF_DAY, 1);
                if (c.after(cmax)) {
                    c.add(Calendar.HOUR_OF_DAY, -1);
                    c.add(Calendar.MINUTE, 1);
                    if (c.after(cmax)) {
                        c.add(Calendar.MINUTE, -1);
                        c.add(Calendar.SECOND, 1);
                        if (c.after(cmax)) {
                            c.add(Calendar.SECOND, -1);
                            c.add(Calendar.MILLISECOND, 1);
                            if (c.after(cmax)) {
                                c.add(Calendar.MILLISECOND, -1);
                            }
                        }
                    }
                }
                gdateb = new GDateBuilder(c);
            }
        }
        gdateb.setBuiltinTypeCode(sType.getPrimitiveType().getBuiltinTypeCode());
        if (pick(2) == 0) {
            gdateb.clearTimeZone();
        }
        return gdateb.toString();
    }

    private void processSimpleType(SchemaType stype, XmlCursor xmlc, String elementName, String serviceName) {
        if (_soapEnc) {
            QName typeName = stype.getName();
            if (typeName != null) {
                xmlc.insertAttributeWithValue(XSI_TYPE, formatQName(xmlc, typeName));
            }
        }
        String sample = null;
        if (elementName != null) {
            sample = Utility.readXMLFile(serviceName, elementName, "Configurations/sampleValues.xml");
            //TBD, to remove the following code when we have sample values for services defined in the xml file
            if (sample == null)
                sample = Utility.readXMLFile("Generic", elementName, "Configurations/sampleValues.xml");
        }
        if (sample == null) {
            sample = sampleDataForSimpleType(stype);  //here returns the final value, NOTE BY FUGUO.
        }
        xmlc.insertChars(sample);
    }

    private void processAttributes(SchemaType stype, XmlCursor xmlc) {
        if (_soapEnc) {
            QName typeName = stype.getName();
            if (typeName != null) {
                xmlc.insertAttributeWithValue(XSI_TYPE, formatQName(xmlc, typeName));
            }
        }
        SchemaProperty[] attrProps = stype.getAttributeProperties();
        for (int i = 0; i < attrProps.length; i++) {
            SchemaProperty attr = attrProps[i];
            if (attr.getMinOccurs().intValue() == 0 && ignoreOptional) {
                continue;
            }
            if (attr.getName().equals(new QName("http://www.w3.org/2005/05/xmlmime", "contentType"))) {
                xmlc.insertAttributeWithValue(attr.getName(), "application/?");
                continue;
            }
            if (_soapEnc) {
                if (SKIPPED_SOAP_ATTRS.contains(attr.getName())) {
                    continue;
                }
                if (ENC_ARRAYTYPE.equals(attr.getName())) {
                    SOAPArrayType arrayType = ((SchemaWSDLArrayType) stype.getAttributeModel().getAttribute(
                            attr.getName())).getWSDLArrayType();
                    if (arrayType != null) {
                        xmlc.insertAttributeWithValue(attr.getName(),
                                formatQName(xmlc, arrayType.getQName()) + arrayType.soap11DimensionString());
                    }
                    continue;
                }
            }
            String value = null;
            if (multiValues != null) {
                String[] values = multiValues.get(attr.getName());
                if (values != null) {
                    value = StringUtils.join(values, ",");
                }
            }
            if (value == null) {
                value = attr.getDefaultText();
            }
            if (value == null) {
                value = sampleDataForSimpleType(attr.getType());
            }
            xmlc.insertAttributeWithValue(attr.getName(), value);
        }
    }

    private ArrayList<Integer> determineMinMaxForSample(SchemaParticle sp, XmlCursor xmlc) {
        ArrayList<Integer> result = new ArrayList<Integer>();

        int minOccurs = sp.getIntMinOccurs();
        result.add(minOccurs);
        int maxOccurs = sp.getIntMaxOccurs();
        /*
         Parameter tempParameter =null;
         if (sp.getName() != null) {
         tempParameter = new Parameter();
         tempParameter.setName(sp.getName().getLocalPart());
        
         }
         */
        // if (!ifNestParameter) 
        /*
         if (!processSequence)
         if (minOccurs >0) {
         parameter.setCompulsory(true);
         AnalysisStats.compulsoryInputParameterList.add(parameter);
         }
         else {
         parameter.setCompulsory(false);
         AnalysisStats.optionalInputParameterList.add(parameter);
         }
         */
        if (minOccurs == maxOccurs) {
            result.add(minOccurs);
            return result;
        }
        if (minOccurs == 0 && ignoreOptional) {
            result.add(minOccurs);
            return result;
        }
        result.add(minOccurs);
        if (result.get(1) == 0) {
            result.set(1, 1);
        }
        if (sp.getParticleType() != SchemaParticle.ELEMENT) {
            return result;
        }
        // it probably only makes sense to put comments in front of individual
        // elements that repeat
        if (!_skipComments) {
            if (sp.getMaxOccurs() == null) {
                // xmlc.insertComment("The next " + getItemNameOrType(sp, xmlc) + "
                // may
                // be repeated " + minOccurs + " or more times");
                if (minOccurs == 0) {
                    xmlc.insertComment("Zero or more repetitions:");
                } else {
                    xmlc.insertComment(minOccurs + " or more repetitions:");
                }
            } else if (sp.getIntMaxOccurs() > 1) {
                xmlc.insertComment(minOccurs + " to " + String.valueOf(sp.getMaxOccurs()) + " repetitions:");
            } else {
                xmlc.insertComment("Optional:");
            }
        }
        return result;
    }

    private void addElementTypeAndRestricionsComment(SchemaLocalElement element, XmlCursor xmlc) {
        SchemaType type = element.getType();
        if (_typeComment && (type != null && type.isSimpleType())) {
            String info = "";
            XmlAnySimpleType[] values = type.getEnumerationValues();
            if (values != null && values.length > 0) {
                info = " - enumeration: [";
                for (int c = 0; c < values.length; c++) {
                    if (c > 0) {
                        info += ",";
                    }
                    info += values[c].getStringValue();
                }
                info += "]";
            }
            if (type.isAnonymousType()) {
                xmlc.insertComment("anonymous type" + info);
            } else {
                xmlc.insertComment("type: " + type.getName().getLocalPart() + info);
            }
        }
    }

    private DefaultMutableTreeNode processElement(SchemaParticle sp, XmlCursor xmlc, boolean mixed, DefaultMutableTreeNode parentNode, List<String> parametersToBeRemoved, List<Parameter> parametersToTry, String serviceName, String compulsory) {
        // cast as schema local element
        SchemaLocalElement element = (SchemaLocalElement) sp;
        String elementName = element.getName().getLocalPart();
        String elementType = null;
        if (element.getType() != null && element.getType().getName() != null) {
            elementType = element.getType().getName().getLocalPart();
        }

        /*
         if (parametersToTry != null) {
         if (ignoreThisParameter(element.getType(), elementName, elementType, parametersToBeRemoved, parametersToTry, serviceName)) {
         parentNode = createSampleForType(element.getType(), xmlc, parentNode, elementName, elementType, parametersToBeRemoved, parametersToTry, serviceName, compulsory);
         } else {
         //do this as well
         }            
         } 
         */
        //if ((parametersToTry == null) || (parametersToTry != null && !ignoreThisParameter(element.getType(), elementName, elementType, parametersToBeRemoved, parametersToTry, serviceName)))
        //{
        //System.out.println("the attribute is............................"+element.getName().getLocalPart()+".....................");
        //System.out.println("the type is............................"+element.getType().getName()+".....................");
        // Add comment about type
        addElementTypeAndRestricionsComment(element, xmlc);
        // / ^ -> <elemenname></elem>^
        if (_soapEnc) {
            xmlc.insertElement(element.getName().getLocalPart()); // soap
        } // encoded?
        // drop
        // namespaces.
        else {
            //here write the data  <v8:LanguageCode>profundum quippe ferant</v8:LanguageCode>  NOTE BY FUGUO
            if ((parametersToTry == null) || (parametersToTry != null 
                    && !ignoreThisParameter(element.getType(), elementName, elementType, parametersToBeRemoved, parametersToTry, serviceName))
                    || ((parametersToTry != null) && (sp.isDefault())))
                xmlc.insertElement(element.getName().getLocalPart(), element.getName().getNamespaceURI());
        }
        // / -> <elem>^</elem>
        // processAttributes( sp.getType(), xmlc );
        xmlc.toPrevToken(); //THIS NAVIGATES TO THE NEXT to add value, NOTE BY FUGUO        
        // -> <elem>stuff^</elem>
        String[] values = null;
        if (multiValues != null) {
            values = multiValues.get(element.getName());
        }
        if (values != null) {
            xmlc.insertChars(StringUtils.join(values, ","));
        } else if (sp.isDefault()) {
            xmlc.insertChars(sp.getDefaultText());
        } else {
            //THIS REALLY GENERATES THE VALUE, FOR EXAMPLE, IF IT IS Language code(String), 
            // it will call the process simple to process, otherwise, keeping going until reaching the simiple one
            parentNode = createSampleForType(element.getType(), xmlc, parentNode, elementName, elementType, parametersToBeRemoved, parametersToTry, serviceName, compulsory);
        }

        //else 
        //    parentNode = createSampleForType(element.getType(), xmlc, parentNode, elementName, elementType, parametersToBeRemoved, parametersToTry, serviceName, compulsory);
        // -> <elem>stuff</elem>^
        xmlc.toNextToken();//THIS NAVIGATES TO THE NEXT, NOTE BY FUGUO
        return parentNode;
    }

    private DefaultMutableTreeNode processSequence(SchemaParticle sp, XmlCursor xmlc, boolean mixed, DefaultMutableTreeNode parentNode, List<String> parametersToBeRemoved, List<Parameter> parametersToTry, String serviceName) {
        SchemaParticle[] spc = sp.getParticleChildren();
        for (int i = 0; i < spc.length; i++) {
// / <parent>maybestuff^</parent>
            parentNode = processParticle(spc[i], xmlc, mixed, parentNode, parametersToBeRemoved, parametersToTry, serviceName);
// <parent>maybestuff...morestuff^</parent>
            if (mixed && i < spc.length - 1) {
                xmlc.insertChars(pick(WORDS));
            }
        }
        return parentNode;
    }

    private void processChoice(SchemaParticle sp, XmlCursor xmlc, boolean mixed, List<String> parametersToBeRemoved, List<Parameter> parametersToTry, String serviceName) {
        SchemaParticle[] spc = sp.getParticleChildren();
        if (!_skipComments) {
            xmlc.insertComment("You have a CHOICE of the next " + String.valueOf(spc.length) + " items at this level");
        }
        for (int i = 0; i < spc.length; i++) {
            processParticle(spc[i], xmlc, mixed, null, parametersToBeRemoved, parametersToTry, serviceName);
        }
    }

    private void processAll(SchemaParticle sp, XmlCursor xmlc, boolean mixed, List<String> parametersToBeRemoved, List<Parameter> parametersToTry, String serviceName) {
        SchemaParticle[] spc = sp.getParticleChildren();
        if (!_skipComments) {
            xmlc.insertComment("You may enter the following " + String.valueOf(spc.length) + " items in any order");
        }
        for (int i = 0; i < spc.length; i++) {
            processParticle(spc[i], xmlc, mixed, null, parametersToBeRemoved, parametersToTry, serviceName);
            if (mixed && i < spc.length - 1) {
                xmlc.insertChars(pick(WORDS));
            }
        }
    }

    private void processWildCard(SchemaParticle sp, XmlCursor xmlc, boolean mixed) {
        if (!_skipComments) {
            xmlc.insertComment("You may enter ANY elements at this point");
        }
// xmlc.insertElement("AnyElement");
    }

    /**
     * Cursor position: Before this call: <outer><foo/>^</outer> (cursor at the
     * ^) After this call: <<outer><foo/><bar/>som text<etc/>^</outer>
     */
    private DefaultMutableTreeNode processParticle(SchemaParticle sp, XmlCursor xmlc, boolean mixed, DefaultMutableTreeNode parentNode, List<String> parametersToBeRemoved, List<Parameter> parametersToTry, String serviceName) {
        ArrayList<Integer> result = determineMinMaxForSample(sp, xmlc);
        String compulsory = null;
        int minOccur = result.get(0);
        if (minOccur > 0) {
            compulsory = "true";
        } else {
            compulsory = "false";
        }

        int loop = result.get(1);

        while (loop-- > 0) {
            switch (sp.getParticleType()) {
                case (SchemaParticle.ELEMENT):
                    parentNode = processElement(sp, xmlc, mixed, parentNode, parametersToBeRemoved, parametersToTry, serviceName, compulsory);
                    break;
                case (SchemaParticle.SEQUENCE):
                    parentNode = processSequence(sp, xmlc, mixed, parentNode, parametersToBeRemoved, parametersToTry, serviceName);
                    break;
                case (SchemaParticle.CHOICE):
                    processChoice(sp, xmlc, mixed, parametersToBeRemoved, parametersToTry, serviceName);
                    break;
                case (SchemaParticle.ALL):
                    processAll(sp, xmlc, mixed, parametersToBeRemoved, parametersToTry, serviceName);
                    break;
                case (SchemaParticle.WILDCARD):
                    processWildCard(sp, xmlc, mixed);
                    break;
                default:
                // throw new Exception("No Match on Schema Particle Type: " +
                // String.valueOf(sp.getParticleType()));
            }
        }
        return parentNode;
    }

    public boolean ignoreThisParameter(SchemaType stype, String elementName, String elementType, List<String> parametersToBeRemoved, List<Parameter> parametersToTry, String serviceName) {
        _exampleContent = SoapUI.getSettings().getBoolean(WsdlSettings.XML_GENERATION_TYPE_EXAMPLE_VALUE);
        _typeComment = SoapUI.getSettings().getBoolean(WsdlSettings.XML_GENERATION_TYPE_COMMENT_TYPE);
        _skipComments = SoapUI.getSettings().getBoolean(WsdlSettings.XML_GENERATION_SKIP_COMMENTS);
        QName nm = stype.getName();
        if (nm == null && stype.getContainerField() != null) {
            nm = stype.getContainerField().getName();
        }

        Parameter parameter = new Parameter();
        parameter.setParameterUniqueIDinTree(AnalysisStats.parameterUniqueIDinTree + 1);
        String nodeName = null;
        if (elementName != null) {
            nodeName = elementName;
        }
        if (elementType != null) {
            parameter.setType(elementType);
        }

        if (nodeName == null) {
            if (stype.getName() != null) {
                nodeName = stype.getName().getLocalPart();
            } else if (nm != null) {
                nodeName = nm.getLocalPart();
            }
        }

        parameter.setName(nodeName);

        if (parametersToTry != null && parametersToTry.contains(parameter)) {
            return false;
            //for (Parameter tempParameter : parametersToTry) {
            //    if (tempParameter.equals(parameter)) {

            //    }
            // }
        }
        return true;
    }

    /**
     * Cursor position Before: <theElement>^</theElement> After:
     * <theElement><lots of stuff/>^</theElement>
     */
    public DefaultMutableTreeNode createSampleForType(SchemaType stype, XmlCursor xmlc, DefaultMutableTreeNode parentNode, String elementName, String elementType, List<String> parametersToBeRemoved, List<Parameter> parametersToTry, String serviceName, String compulsory) {
        _exampleContent = SoapUI.getSettings().getBoolean(WsdlSettings.XML_GENERATION_TYPE_EXAMPLE_VALUE);
        _typeComment = SoapUI.getSettings().getBoolean(WsdlSettings.XML_GENERATION_TYPE_COMMENT_TYPE);
        _skipComments = SoapUI.getSettings().getBoolean(WsdlSettings.XML_GENERATION_SKIP_COMMENTS);
        QName nm = stype.getName();
        if (nm == null && stype.getContainerField() != null) {
            nm = stype.getContainerField().getName();
        }

        if (nm != null && excludedTypes.contains(nm)) {
            if (!_skipComments) {
                xmlc.insertComment("Ignoring type [" + nm + "]");
            }
            return null;
        }

        if (_typeStack.contains(stype)) {
            return null;
        }

        DefaultMutableTreeNode complexNode = null;  // to be deleted        
        Parameter parameter = new Parameter();
        AnalysisStats.parameterUniqueIDinTree = AnalysisStats.parameterUniqueIDinTree + 1;
        parameter.setParameterUniqueIDinTree(AnalysisStats.parameterUniqueIDinTree);
        _typeStack.add(stype); //stype is usde to keep track of every element
        try { //complex data type will by pass this unitl it reaches the leaf note which is simple data type NOTE BY FUGUO
//            SchemaLocalElement element =null;
//            String nodeName =null;
//            if (stype.getContentModel() != null && stype.getContentModel().getParticleType() == SchemaParticle.ELEMENT) {
//                element = (SchemaLocalElement)stype.getContentModel();
//                nodeName = element.getName().getLocalPart()+ "("+stype.getName().getLocalPart()+")";                
//             }
            String nodeName = null;
            if (elementName != null) {
                nodeName = elementName;
            }

            //if (elementType !=null)
            //    nodeName = nodeName+":"+elementType;
            if (elementType != null) {
                parameter.setType(elementType);
            }

            if (nodeName == null) {
                if (stype.getName() != null) {
                    nodeName = stype.getName().getLocalPart();
                } else if (nm != null) {
                    nodeName = nm.getLocalPart();
                }
            }

            parameter.setName(nodeName);
            if (parentNode != null) {
                parameter.setParentParameter((Parameter) parentNode.getUserObject());
            }

            if (stype.isSimpleType() || stype.isURType()) {
                //System.out.print("the element is----------------" + elm.getName().getLocalPart());
                //System.out.print("----------Star a Simple Type: "+nodeName+")----------------------------");
                if (parametersToTry == null && parametersToBeRemoved == null) {
                    processSimpleType(stype, xmlc, elementName, serviceName);
                } else if (parametersToTry != null) {
                    for (Parameter tempParameter : parametersToTry) {
                        if (tempParameter.equals(parameter)) {
                            processSimpleType(stype, xmlc, elementName, serviceName);
                            break;
                        }
                    }
                } else if (parametersToBeRemoved != null && !parametersToBeRemoved.contains(elementName)) {
                    processSimpleType(stype, xmlc, elementName, serviceName);
                }

                //if (!(parametersToBeRemoved != null && parametersToBeRemoved.contains(elementName)))
                //   processSimpleType(stype, xmlc, elementName, serviceName);
                AnalysisStats.listofPreliminary.add(elementName);  // fix this to record the level, currently it does not work for the paramerters that occur more than once
                AnalysisStats.Global_total_number_of_parameters = AnalysisStats.Global_total_number_of_parameters + 1;

                //DefaultMutableTreeNode simpleNode = new DefaultMutableTreeNode(element.getName().getLocalPart()+ "("+stype.getName()+")");
                //nodeName = nodeName + ":SIMPLE";
                parameter.setComplex(false);
                if (compulsory != null) {
                    
                    if (compulsory.equals("true") && ((parameter.getParentParameter() != null && (parameter.getParentParameter().getParameterUniqueIDinTree()==1)) 
                            || (parameter.getParentParameter() != null && parameter.getParentParameter().isCompulsory()))) {
                    //if (compulsory.equals("true")) {
                        parameter.setCompulsory(true);
                        AnalysisStats.compulsoryInputParameterList.add(parameter);
                    } else if (compulsory.equals("false")) {
                        parameter.setCompulsory(false);
                        AnalysisStats.optionalInputParameterList.add(parameter);
                    }
                }

                DefaultMutableTreeNode simpleNode = new DefaultMutableTreeNode(parameter);
                simpleNode.setUserObject(parameter);
                if (parentNode != null) {
                    parentNode.add(simpleNode);
                }
                //parentNode.getUserObject()
                /*
                 if (currentNode !=null) {
                 currentNode.add(simpleNode);
                 } else {
                 root.add(simpleNode);
                 } 
                 */
                AnalysisStats.Global_total_number_of_simple_parameters = AnalysisStats.Global_total_number_of_simple_parameters + 1;                
                parameter.setSimpleIndex(AnalysisStats.Global_total_number_of_simple_parameters-1);
                AnalysisStats.simpleParameterList.add(parameter);
            } else {
                //nodeName = nodeName + ":COMPLEX";
                parameter.setComplex(true);

                if (compulsory != null) {
                    if (compulsory.equals("true") && ((parameter.getParentParameter() != null && parameter.getParentParameter().getParameterUniqueIDinTree()==1)
                            || (parameter.getParentParameter() != null && parameter.getParentParameter().isCompulsory()))) {
                    //if (compulsory.equals("true")) {                    
                        parameter.setCompulsory(true);                        
                        AnalysisStats.compulsoryInputParameterList.add(parameter);
                    } else if (compulsory.equals("false")) {
                        parameter.setCompulsory(false);
                        AnalysisStats.optionalInputParameterList.add(parameter);
                    }
                }

                complexNode = new DefaultMutableTreeNode(parameter);
                complexNode.setUserObject(parameter);

                /*
                 if (currentNode !=null) {
                 currentNode.add(complexNode1);
                 } else {
                 DefaultMutableTreeNode bigNote = new DefaultMutableTreeNode(stype.getName().getLocalPart())
                 bigNote.add(complexNode1);
                 }
                
                 currentNode = complexNode;
                 */
                if (parentNode != null) {
                    parentNode.add(complexNode);
                }

                // System.out.print("the attribute is............................"+element.getName().getLocalPart()+".....................");                                       
                //System.out.print("--------------Star a Complex Type: "+ nodeName+"----------------------------");            
                AnalysisStats.Global_total_number_of_complex_parameters = AnalysisStats.Global_total_number_of_complex_parameters + 1;
                AnalysisStats.complexParameterList.add(parameter);
            }

            // complex Type, simple type also comes here.
            // <theElement>^</theElement>
            processAttributes(stype, xmlc);

            // <theElement attri1="string">^</theElement>
            switch (stype.getContentType()) {
                case SchemaType.NOT_COMPLEX_TYPE:
                case SchemaType.EMPTY_CONTENT:
                    // noop
                    break;
                case SchemaType.SIMPLE_CONTENT: {
                    if (parametersToTry == null && parametersToBeRemoved == null) {
                        processSimpleType(stype, xmlc, elementName, serviceName);
                    } else if (parametersToTry != null) {
                        for (Parameter tempParameter : parametersToTry) {
                            if (tempParameter.equals(parameter)) {
                                processSimpleType(stype, xmlc, elementName, serviceName);
                                break;
                            }
                        }
                        //processSimpleType(stype, xmlc, elementName, serviceName);
                    } else if (parametersToBeRemoved != null && !parametersToBeRemoved.contains(elementName)) {
                        processSimpleType(stype, xmlc, elementName, serviceName);
                    }
                }
                break;
                case SchemaType.MIXED_CONTENT:
                    xmlc.insertChars(pick(WORDS) + " ");
                    if (stype.getContentModel() != null) {
                        processParticle(stype.getContentModel(), xmlc, true, complexNode, parametersToBeRemoved, parametersToTry, serviceName);
                    }
                    xmlc.insertChars(pick(WORDS));
                    break;
                case SchemaType.ELEMENT_CONTENT:
                    if (stype.getContentModel() != null) {
                        //SchemaLocalElement element = (SchemaLocalElement) sp
                        processParticle(stype.getContentModel(), xmlc, false, complexNode, parametersToBeRemoved, parametersToTry, serviceName);
                    }
                    break;
            }
        } finally {
            //everytime it hits here, it is either hits a simple data, or finishes one complex data type
            _typeStack.remove(_typeStack.size() - 1);
            //System.out.print("the stack size:"+_typeStack.size());            
            if (_typeStack.size() == 1) {
                AnalysisStats.Global_total_number_of_levels = AnalysisStats.Global_total_number_of_levels + 1;
                this.counter++;
                //System.out.print("This is "+ this.counter + " th complex object finished--------------------");
            }
        }
        return parentNode;
    }

}
