﻿<?xml version="1.0" encoding="utf-8"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:fo="http://www.w3.org/1999/XSL/Format">
	<!-- Importation des templates communs -->
	<xsl:import href="../../common.xsl"/>
	<xsl:import href="../common.xsl"/>
	
	<!-- Cible XML (XSL-FO) -->
	<xsl:output method="xml" indent="yes" />
	
	<!-- Paramètres-->
	<xsl:param name="idCategorie" />
	<xsl:param name="idPoule" />
	<xsl:param name="afficherPoints" />

	<!-- Template principal -->
	<xsl:template match="/">
		<fo:root>
			<!-- Déclaration de la mise en page -->
			<fo:layout-master-set>
				<fo:simple-page-master master-name="page" margin="1cm">
					<fo:region-body margin="1cm" />
					<fo:region-before extent="1cm" />
					<fo:region-after extent="1cm" />
				</fo:simple-page-master>
			</fo:layout-master-set>

			<!-- Corps du document -->
			<fo:page-sequence master-reference="page">
				<!-- Entete -->
				<fo:static-content flow-name="xsl-region-before">
					<xsl:call-template name="pdf-entete">
					  <xsl:with-param name="titre">
							<xsl:choose>
								<xsl:when test="/concours/@participants = 'equipes'">
									Classement des équipes aux phases qualificatives
								</xsl:when>
								<xsl:otherwise>
									Classement des joueurs aux phases qualificatives
								</xsl:otherwise>
							</xsl:choose>
						</xsl:with-param>
					</xsl:call-template>
				</fo:static-content>

				<!-- Pied de page -->
				<fo:static-content flow-name="xsl-region-after">
					<xsl:call-template name="pdf-pied">
					</xsl:call-template>
				</fo:static-content>

				<!-- Contenu -->
				<fo:flow flow-name="xsl-region-body">
					<fo:block font-size="10pt">
						<!-- Liste des catégories -->
						<xsl:apply-templates select="/concours/listeCategories">
						</xsl:apply-templates>
					</fo:block>
				</fo:flow>
			</fo:page-sequence>
		</fo:root>
	</xsl:template>
	
	<!-- Template d'une liste de catégories -->
	<xsl:template match="listeCategories">
		<xsl:choose>
			<!-- Catégorie non indiquée -->
			<xsl:when test="$idCategorie = ''">
				<xsl:apply-templates select="./categorie">
				</xsl:apply-templates>
			</xsl:when>
			<!-- Catégorie existante -->
			<xsl:when test="count(./categorie[@id=$idCategorie]) = 1">
				<xsl:apply-templates select="./categorie[@id=$idCategorie]">
				</xsl:apply-templates>
			</xsl:when>
			<!-- Catégorie inexistante -->
			<xsl:otherwise>
				<xsl:call-template name="pdf-erreur">
				  <xsl:with-param name="erreur">La catégorie indiquée n'existe pas.</xsl:with-param>
				</xsl:call-template>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>
	
	<!-- Template d'une catégorie -->
	<xsl:template match="categorie">
		<xsl:if test="count(./listePoules/poule/listeParticipants/participant) != 0">
			<!-- Titre -->
			<xsl:if test="count(../categorie) != 1">
				<xsl:call-template name="pdf-titre-h1">
				  <xsl:with-param name="titre"><xsl:value-of select="./@nom" /></xsl:with-param>
				</xsl:call-template>
			</xsl:if>
			
			<!-- Liste des poules -->
			<xsl:apply-templates select="./listePoules">
			</xsl:apply-templates>
		</xsl:if>
	</xsl:template>
	
	<!-- Template d'une liste de poules -->
	<xsl:template match="listePoules">
		<xsl:choose>
			<!-- Poule non indiquée -->
			<xsl:when test="$idPoule = ''">
				<xsl:apply-templates select="./poule">
				</xsl:apply-templates>
			</xsl:when>
			<!-- Poule existante -->
			<xsl:when test="count(./poule[@id=$idPoule]) = 1">
				<xsl:apply-templates select="./poule[@id=$idPoule]">
				</xsl:apply-templates>
			</xsl:when>
			<!-- Poule inexistante -->
			<xsl:otherwise>
				<xsl:call-template name="pdf-erreur">
				  <xsl:with-param name="erreur">La poule indiquée n'existe pas.</xsl:with-param>
				</xsl:call-template>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>
	
	<!-- Template d'une poule -->
	<xsl:template match="poule">
		<xsl:if test="count(./listeParticipants/participant) != 0">
			<!-- Titre -->
			<xsl:if test="count(../poule) != 1">
				<xsl:call-template name="pdf-titre-h2">
				  <xsl:with-param name="titre"><xsl:value-of select="./@nom" /></xsl:with-param>
				</xsl:call-template>
			</xsl:if>
			
			<!-- Liste des participants -->
			<xsl:apply-templates select="./classementPoulePhasesQualificatives/listeClassementParticipants">
			</xsl:apply-templates>
		</xsl:if>
	</xsl:template>
	
	<!-- Template d'un classement de participants -->
	<xsl:template match="listeClassementParticipants">
		<fo:block>
			<fo:table width="100%" border="0.5pt solid black">
				<fo:table-column column-width="15%" />
				<xsl:choose>
					<xsl:when test="$afficherPoints = 1">
						<fo:table-column column-width="70%" />
						<fo:table-column column-width="15%" />
					</xsl:when>
					<xsl:otherwise>
						<fo:table-column column-width="85%" />
					</xsl:otherwise>
				</xsl:choose>

				<fo:table-header>
					<fo:table-row>
						<fo:table-cell border="0.5pt solid black" background-color="#CCCCCC" padding="2mm">
							<fo:block font-weight="bold">Rang</fo:block>
						</fo:table-cell>
						<fo:table-cell border="0.5pt solid black" background-color="#CCCCCC" padding="2mm">
							<fo:block font-weight="bold">
								<xsl:choose>
									<xsl:when test="/concours/@participants = 'equipes'">
										Equipe
									</xsl:when>
									<xsl:otherwise>
										Joueur
									</xsl:otherwise>
								</xsl:choose>
							</fo:block>
						</fo:table-cell>
						<xsl:if test="$afficherPoints = 1">
							<fo:table-cell border="0.5pt solid black" background-color="#CCCCCC" padding="2mm">
								<fo:block font-weight="bold">Points</fo:block>
							</fo:table-cell>
						</xsl:if>
					</fo:table-row>
				</fo:table-header>
				<fo:table-body>
					<xsl:apply-templates select="./classementParticipant">
						<xsl:sort select="@rang" data-type="number" />
					</xsl:apply-templates>
				</fo:table-body>
			</fo:table>
		</fo:block>
	</xsl:template>
	
	<!-- Template d'un participant dans un classement -->
	<xsl:template match="classementParticipant">
		<xsl:variable name="id" select="./@refParticipant" />
		<fo:table-row>
			<fo:table-cell border="0.5pt solid black" padding="2mm">
				<fo:block><xsl:value-of select="./@rang" /></fo:block>
			</fo:table-cell>
			<fo:table-cell border="0.5pt solid black" padding="2mm">
				<fo:block>
					<xsl:if test="//participant[@id=$id]/@stand != ''"><xsl:value-of select="//participant[@id=$id]/@stand" /> - </xsl:if>
					<xsl:value-of select="//participant[@id=$id]/@nom" />
				</fo:block>
			</fo:table-cell>
			<xsl:if test="$afficherPoints = 1">
				<fo:table-cell border="0.5pt solid black" padding="2mm">
					<fo:block><xsl:value-of select="//participant[@id=$id]/@pointsPhasesQualifs" /></fo:block>
				</fo:table-cell>
			</xsl:if>
		</fo:table-row>
	</xsl:template>
</xsl:stylesheet>