package de.rcblum.overcollect.export;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import de.rcblum.overcollect.configuration.OWItem;
import de.rcblum.overcollect.configuration.OWLib;
import de.rcblum.overcollect.data.OWCharacterStats;
import de.rcblum.overcollect.data.OWMatch;
import de.rcblum.overcollect.data.OWMatch.Result;

public class XSSFExporter {
	public static void main(String[] args) {
		XSSFExporter eExport = new XSSFExporter();
		eExport.generateMatchSheet();
		eExport.generateHeroSheets();
		eExport.save(Paths.get("export.xlsx"));

	}

	private List<OWMatch> matches = null;

	private List<String> maps = null;

	private List<OWItem> heroes = null;

	private XSSFWorkbook workbook = null;

	public XSSFExporter() {
		this.matches = OWLib.getInstance().getMatches().stream().filter(m -> m.getAccount() == null || m.getAccount().equals(OWLib.getInstance().getActiveAccount()))
				.sorted((m1, m2) -> m1.getStartTime().compareTo(m2.getStartTime())).collect(Collectors.toList());
		this.heroes = OWLib.getInstance().getHeroes();
		this.maps = OWLib.getInstance().getMaps();
		this.workbook = new XSSFWorkbook();
	}

	private XSSFSheet createHeroSheet(OWItem hero) {
		String heroName = hero.getItemName();
		Set<String> secondaryStats = hero.getOCRConfiguration().secondaryValues.keySet();
		XSSFSheet sheet = null;
		System.out.println("Hero: " + hero.getItemName() + " ");
		List<OWMatch> heroMatches = this.matches;// .stream().filter(m ->
													// m.getCharacterStats().stream().anyMatch(j
													// ->
													// heroName.equals(j.getName()))).collect(Collectors.toList());
		List<OWMatch> foundMatches = this.matches.stream()
				.filter(m -> m.getCharacterStats().stream().anyMatch(j -> heroName.equals(j.getName())))
				.collect(Collectors.toList());
		;
		System.out.println("Matches: " + foundMatches.size());
		System.out.println();
		if (foundMatches.size() > 0) {
			sheet = this.workbook.createSheet(heroName + "_data");
			int rowIndex = 0;
			int cIndex = 0;
			Row row = sheet.createRow(rowIndex++);
			Cell c = row.createCell(cIndex++);
			c.setCellValue("Starttime");
			c = row.createCell(cIndex++);
			c.setCellValue("Endtime");
			c = row.createCell(cIndex++);
			c.setCellValue("Map");
			c = row.createCell(cIndex++);
			c.setCellValue("Team SR");
			c = row.createCell(cIndex++);
			c.setCellValue("Enemy SR");
			c = row.createCell(cIndex++);
			c.setCellValue("Result");
			c = row.createCell(cIndex++);
			c.setCellValue("Winrate");
			// Hero Stats
			c = row.createCell(cIndex++);
			c.setCellValue("Name");
			c = row.createCell(cIndex++);
			c.setCellValue("Eliminations");
			c = row.createCell(cIndex++);
			c.setCellValue("Objective Kills");
			c = row.createCell(cIndex++);
			c.setCellValue("Objective Time");
			c = row.createCell(cIndex++);
			c.setCellValue("Damage Done");
			c = row.createCell(cIndex++);
			c.setCellValue("Healing Done");
			c = row.createCell(cIndex++);
			c.setCellValue("Deaths");
			c = row.createCell(cIndex++);
			c.setCellValue("K/D");
			for (String secondaryStat : secondaryStats) {
				c = row.createCell(cIndex++);
				c.setCellValue(secondaryStat);
			}
			c = row.createCell(cIndex++);
			c.setCellValue("SR after Match");
			c = row.createCell(cIndex++);
			c.setCellValue("SR Difference");
			c = row.createCell(cIndex++);
			c.setCellValue("Match ID");
			CellStyle cellStyleDate = workbook.createCellStyle();
			CellStyle cellStylePercentage = workbook.createCellStyle();
			CreationHelper createHelper = workbook.getCreationHelper();
			cellStyleDate.setDataFormat(createHelper.createDataFormat().getFormat("DD.MM.YYYY hh:mm:ss"));
			cellStylePercentage.setDataFormat(createHelper.createDataFormat().getFormat("0%"));

			OWMatch previousMatch = null;
			double kills = 0;
			int deaths = 0;
			double wins = 0;
			int games = 0;
			for (OWMatch match : heroMatches) {
				if (match.getCharacterStats().stream().anyMatch(j -> heroName.equals(j.getName()))) {
					row = sheet.createRow(rowIndex++);
					int cellIndex = 0;
					c = row.createCell(cellIndex++);
					c.setCellValue(match.getStartTime());
					c.setCellStyle(cellStyleDate);
					c = row.createCell(cellIndex++);
					c.setCellValue(match.getEndTime());
					c.setCellStyle(cellStyleDate);
					c = row.createCell(cellIndex++);
					c.setCellValue(match.getMap());
					c = row.createCell(cellIndex++);
					c.setCellValue(match.getTeamSr());
					c = row.createCell(cellIndex++);
					c.setCellValue(match.getEnemySr());
					c = row.createCell(cellIndex++);
					c.setCellValue(match.getResult().toString());
					games++;
					wins += (match.getResult() == Result.VICTORY ? 1 : 0);
					c = row.createCell(cellIndex++);
					c.setCellValue(wins / games);
					c.setCellStyle(cellStylePercentage);
					// Hero Stats
					Optional<OWCharacterStats> statOptional = match.getCharacterStats().stream()
							.filter(i -> i.getName().equals(heroName)).findFirst();
					c = row.createCell(cellIndex++);
					c.setCellValue(statOptional.isPresent() ? statOptional.get().getName() : "");
					c = row.createCell(cellIndex++);
					c.setCellValue(statOptional.isPresent() ? statOptional.get().getEliminations() : 0);
					c = row.createCell(cellIndex++);
					c.setCellValue(statOptional.isPresent() ? statOptional.get().getObjectiveKills() : 0);
					c = row.createCell(cellIndex++);
					c.setCellValue(statOptional.isPresent() ? statOptional.get().getObjectiveTime() : "");
					c = row.createCell(cellIndex++);
					c.setCellValue(statOptional.isPresent() ? statOptional.get().getDamageDone() : 0);
					c = row.createCell(cellIndex++);
					c.setCellValue(statOptional.isPresent() ? statOptional.get().getHealingDone() : 0);
					c = row.createCell(cellIndex++);
					c.setCellValue(statOptional.isPresent() ? statOptional.get().getDeaths() : 0);
					deaths += statOptional.get().getDeaths();
					kills += statOptional.get().getEliminations();
					double kd = kills / (deaths == 0 ? 1 : deaths);
					c = row.createCell(cellIndex++);
					c.setCellValue(kd);
					Map<String, Integer> sStats = statOptional.isPresent() ? statOptional.get().getSecondaryStats()
							: new HashMap<>();
					for (String secondaryStat : secondaryStats) {
						c = row.createCell(cellIndex++);
						if (sStats != null && sStats.get(secondaryStat) != null)
							c.setCellValue(sStats.get(secondaryStat));
					}
					c = row.createCell(cellIndex++);
					c.setCellValue(match.getSr());
					c = row.createCell(cellIndex++);
					c.setCellValue(match.getSr() - (previousMatch != null && previousMatch.getSr() != -1
							? previousMatch.getSr() : match.getSr()));
					c = row.createCell(cellIndex++);
					c.setCellValue(match.getMatchId());
				}
				previousMatch = match;
			}
		}
		return sheet;
	}

	public void generateHeroSheets() {
		for (OWItem hero : heroes) {
			this.createHeroSheet(hero);
		}
	}

	public XSSFSheet generateMatchSheet() {
		XSSFSheet sheet = null;
		sheet = workbook.createSheet("match_data");
		int rowIndex = 0;
		Row row = sheet.createRow(rowIndex++);
		Cell c = row.createCell(0);
		c.setCellValue("Starttime");
		c = row.createCell(1);
		c.setCellValue("Endtime");
		c = row.createCell(2);
		c.setCellValue("Map");
		c = row.createCell(3);
		c.setCellValue("Team SR");
		c = row.createCell(4);
		c.setCellValue("Enemy SR");
		c = row.createCell(5);
		c.setCellValue("Result");
		c = row.createCell(6);
		c.setCellValue("SR after Match");
		c = row.createCell(7);
		c.setCellValue("SR Difference");
		c = row.createCell(8);
		c.setCellValue("Match ID");
		CellStyle cellStyle = workbook.createCellStyle();
		CreationHelper createHelper = workbook.getCreationHelper();
		cellStyle.setDataFormat(createHelper.createDataFormat().getFormat("DD.MM.YYYY hh:mm:ss"));
		// Set values
		OWMatch previousMatch = null;
		for (OWMatch match : matches) {
			row = sheet.createRow(rowIndex++);
			int cellIndex = 0;
			c = row.createCell(cellIndex++);
			c.setCellValue(match.getStartTime());
			c.setCellStyle(cellStyle);
			c = row.createCell(cellIndex++);
			c.setCellValue(match.getEndTime());
			c.setCellStyle(cellStyle);
			c = row.createCell(cellIndex++);
			c.setCellValue(match.getMap());
			c = row.createCell(cellIndex++);
			c.setCellValue(match.getTeamSr());
			c = row.createCell(cellIndex++);
			c.setCellValue(match.getEnemySr());
			c = row.createCell(cellIndex++);
			c.setCellValue(match.getResult().toString());
			c = row.createCell(cellIndex++);
			c.setCellValue(match.getSr());
			c = row.createCell(cellIndex++);
			c.setCellValue(match.getSr() - (previousMatch != null ? previousMatch.getSr() : match.getSr()));
			c = row.createCell(cellIndex++);
			c.setCellValue(match.getMatchId());
			previousMatch = match;
		}
		return sheet;
	}

	public void save(Path path) {
		try (OutputStream out = Files.newOutputStream(path)) {
			workbook.write(out);
			System.out.println("Excel written successfully..");
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
