import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.dataformat.xml.XmlMapper
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import com.fasterxml.jackson.databind.JsonNode
import java.io.File
import java.io.StringReader
import javax.xml.XMLConstants
import javax.xml.transform.stream.StreamSource
import javax.xml.validation.SchemaFactory

fun main(args: Array<String>) {
    if (args.size != 2) {
        println("Input two files in the format <input-file> <output-file>")
        return
    }
    val inputFile = args[0]
    val outputFile = args[1]

    val isSourceXML = inputFile.endsWith(".xml")
    val inputString = File(inputFile).readText()
    if(isSourceXML) {
        if(compareSchema(inputString)) {
            val outPutString = convertXmlToJson(inputString)
            File(outputFile).writeText(outPutString)
        } else {
            println("Not a valid xml schema")
        }
    } else {
        val outPutString = convertJsonToXml(inputString)
        File(outputFile).writeText(outPutString)
    }
    println("Conversion completed successfully. File saved at $outputFile")
}

fun convertXmlToJson(xmlString: String): String {
    val xmlMapper = XmlMapper().apply { registerKotlinModule() }
    val jsonMapper = ObjectMapper()

    val xmlTree = xmlMapper.readTree("<tag>" + xmlString + "</tag>")
    return jsonMapper.writeValueAsString(xmlTree)
}

fun convertJsonToXml(jsonString: String): String {
    val objectMapper = ObjectMapper()
    val xmlMapper = XmlMapper()
    xmlMapper.enable(SerializationFeature.INDENT_OUTPUT)

    // Read the JSON string into a JsonNode
    val jsonNode: JsonNode = objectMapper.readTree(jsonString)

    // Convert the JsonNode to an XML string
    val xmlString = xmlMapper.writeValueAsString(jsonNode)

    // Cleanup/Remove the <ObjectNode> and </ObjectNode> tags from the output
    return xmlString.replaceFirst("<ObjectNode>", "").replaceFirst("</ObjectNode>", "")
}

fun compareSchema(xmlString: String): Boolean {
    val schemaEx = """<?xml version="1.0" encoding="UTF-8"?>
        <xs:schema xmlns:xs="http://www.w3.org/2001/XMLSchema">
            <xs:element name="AddressBook">
                <xs:complexType>
                    <xs:sequence>
                        <xs:element name="Contact" maxOccurs="unbounded">
                            <xs:complexType>
                                <xs:all>
                                    <xs:element name="CustomerID" type="xs:string"/>
                                    <xs:element name="CompanyName" type="xs:string"/>
                                    <xs:element name="ContactName" type="xs:string"/>
                                    <xs:element name="ContactTitle" type="xs:string"/>
                                    <xs:element name="Address" type="xs:string"/>
                                    <xs:element name="City" type="xs:string"/>
                                    <xs:element name="Email" type="xs:string"/>
                                    <xs:element name="PostalCode" type="xs:string" minOccurs="0"/>
                                    <xs:element name="Country" type="xs:string" minOccurs="0"/>
                                    <xs:element name="Phone" type="xs:string"/>
                                    <xs:element name="Fax" type="xs:string" minOccurs="0"/>
                                    <xs:element name="Region" type="xs:string" minOccurs="0"/>
                                </xs:all>
                            </xs:complexType>
                        </xs:element>
                    </xs:sequence>
                </xs:complexType>
            </xs:element>
        </xs:schema>"""

    val schemaFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI)
    val schema = schemaFactory.newSchema(StreamSource(StringReader(schemaEx)))
    val validator = schema.newValidator()
    try {
        validator.validate(StreamSource(StringReader(xmlString)))
        println("XML is valid.")
        return true
    } catch (e: Exception) {
        println("XML is not valid: ${e.message}")
        return false
    }
}