<?xml version="1.0" encoding="utf-8"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
    
    <xsl:output method="xml" version="1.0" encoding="utf-8" indent="yes"
        xalan:indent-amount="2" xmlns:xalan="http://xml.apache.org/xalan" />

    <xsl:template match="/">
        <link xmlns="http://com.zaubersoftware.leviathan.api.engine/ns/2011/01">
            <xsl:attribute name="title">
                <xsl:value-of select="/html/head/title"></xsl:value-of>
            </xsl:attribute>
            <description>
                <xsl:value-of select="/html/head/meta/@content[../@name = 'description']"></xsl:value-of>
            </description>
            <body>
                <xsl:apply-templates select="/html/body"/>
            </body>
            <categories>
                <category>Autos</category>
                <category>Ropa</category>
                <category>Motos</category>
                <category>Musica</category>
            </categories>
        </link>
    </xsl:template>
      
    <xsl:template match="script"></xsl:template>
    
</xsl:stylesheet>