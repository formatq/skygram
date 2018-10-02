<xsl:stylesheet version="2.0"
                xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

    <xsl:template match="@*|node()">
        <xsl:copy>
            <xsl:apply-templates select="@*|node()"/>
        </xsl:copy>
    </xsl:template>

    <xsl:template match="//*[local-name()='quote']">
        <pre>
            <xsl:value-of select="./legacyquote[1]"/>
            <xsl:value-of select="./text()"/>
        </pre>
    </xsl:template>

    <xsl:template match="/text()">
        <xsl:copy/>
    </xsl:template>
</xsl:stylesheet>
