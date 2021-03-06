package org.hwyl.sexytopo.control.io.basic;

import android.content.Context;
import android.util.Log;

import org.hwyl.sexytopo.SexyTopo;
import org.hwyl.sexytopo.control.io.Util;
import org.hwyl.sexytopo.model.sketch.Sketch;
import org.hwyl.sexytopo.model.survey.Leg;
import org.hwyl.sexytopo.model.survey.Station;
import org.hwyl.sexytopo.model.survey.Survey;
import org.json.JSONException;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;


public class Loader {


    public static Survey loadSurvey(Context context, String name) throws Exception {
        return loadSurvey(context, name, false);
    }


    public static Survey restoreAutosave(Context context, String name) throws Exception {
        return loadSurvey(context, name, true);
    }


    private static Survey loadSurvey(Context context, String name, boolean restoreAutosave)
            throws Exception {
        Set<String> surveyNamesNotToLoad = new HashSet<>();
        return loadSurvey(context, name, surveyNamesNotToLoad, restoreAutosave);
    }


    public static Survey loadSurvey(Context context, String name,
                                     Set<String> surveyNamesNotToLoad, boolean restoreAutosave)
            throws Exception {
        Survey survey = new Survey(name);
        loadSurveyData(context, name, survey, restoreAutosave);
        loadSketches(context, survey, restoreAutosave);
        surveyNamesNotToLoad.add(name);
        loadMetadata(context, survey, surveyNamesNotToLoad, restoreAutosave);
        survey.setSaved(true);
        return survey;
    }

    private static void loadMetadata(Context context, Survey survey,
                                     Set<String> surveyNamesNotToLoad, boolean restoreAutosave)
            throws Exception {
        String metadataPath = Util.getPathForSurveyFile(context, survey.getName(),
                getExtension(SexyTopo.METADATA_EXTENSION, restoreAutosave));
        if (Util.doesFileExist(metadataPath)) {
            String metadataText = slurpFile(metadataPath);
            MetadataTranslater.translateAndUpdate(
                    context, survey, metadataText, surveyNamesNotToLoad);
        }
    }


    private static void loadSurveyData(Context context, String name, Survey survey,
                                       boolean restoreAutosave) {
        String path = Util.getPathForSurveyFile(context, name,
                getExtension("svx", restoreAutosave));
        String text = slurpFile(path);
        parse(text, survey);
    }


    private static void loadSketches(Context context, Survey survey, boolean restoreAutosave)
            throws JSONException {

        String planPath = Util.getPathForSurveyFile(context, survey.getName(),
                getExtension(SexyTopo.PLAN_SKETCH_EXTENSION, restoreAutosave));
        if (Util.doesFileExist(planPath)) {
            String planText = slurpFile(planPath);
            Sketch plan = SketchJsonTranslater.translate(survey, planText);
            survey.setPlanSketch(plan);
        }

        String elevationPath = Util.getPathForSurveyFile(context, survey.getName(),
                getExtension(SexyTopo.EXT_ELEVATION_SKETCH_EXTENSION, restoreAutosave));
        if (Util.doesFileExist(elevationPath)) {
            String elevationText = slurpFile(elevationPath);
            Sketch elevation = SketchJsonTranslater.translate(survey, elevationText);
            survey.setElevationSketch(elevation);
        }
    }


    public static String slurpFile(File file) {
        return slurpFile(file.getAbsolutePath());
    }

    public static String slurpFile(String filename) {

        StringBuilder text = new StringBuilder();

        try {
            File file = new File(filename);
            BufferedReader br = new BufferedReader(new FileReader(file));
            String line;

            while ((line = br.readLine()) != null) {
                text.append(line);
                text.append('\n');
            }

        } catch (Exception e) {
            Log.d(SexyTopo.TAG, "Error trying to read " + filename + ": " + e.getMessage());
        }
        return text.toString();
    }

    private static void parse(String text, Survey survey) {

        Map<String, Station> nameToStation = new HashMap<>();

        Station origin = survey.getOrigin();
        nameToStation.put(origin.getName(), origin);

        String[] lines = text.split("\n");
        for (String line : lines) {
            if (line.trim().equals("")) {
                continue;
            }
            String[] fields = line.trim().split("\t");
            addLegToSurvey(survey, nameToStation, fields);
        }
    }

    private static void addLegToSurvey(Survey survey,
            Map<String, Station> nameToStation, String[] fields) {

        Station from = retrieveOrCreateStation(nameToStation, fields[0]);
        Station to = retrieveOrCreateStation(nameToStation, fields[1]);

        double distance = Double.parseDouble(fields[2]);
        double azimuth = Double.parseDouble(fields[3]);
        double inclination = Double.parseDouble(fields[4]);

        Leg leg = (to == Survey.NULL_STATION)?
            new Leg(distance, azimuth, inclination) :
            new Leg(distance, azimuth, inclination, to);

        from.addOnwardLeg(leg);

        // FIXME: bit of a hack; hopefully the last station processed will be the active one
        // (should probably record the active station in the file somewhere)
        survey.setActiveStation(from);
    }

    private static Station retrieveOrCreateStation(Map<String, Station> nameToStation,
                                                   String name) {
        if (name.equals(SexyTopo.BLANK_STATION_NAME)) {
            return Survey.NULL_STATION;
        } else if (nameToStation.containsKey(name)) {
            return nameToStation.get(name);
        } else {
            Station station = new Station(name);
            nameToStation.put(name, station);
            return station;
        }
    }

    private static String getExtension(String baseExtention, boolean isAutosave) {
        if (isAutosave) {
            return baseExtention + "." + SexyTopo.AUTOSAVE_EXTENSION;
        } else {
            return baseExtention;
        }
    }


}
