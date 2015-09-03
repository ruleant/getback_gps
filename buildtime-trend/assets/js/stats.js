/* vim: set expandtab sw=4 ts=4: */
/**
 * Analyse and visualise trend data using the Keen.io API.
 *
 * Copyright (C) 2014-2015 Dieter Adriaenssens <ruleant@users.sourceforge.net>
 *
 * This file is part of buildtimetrend/dashboard
 * <https://github.com/buildtimetrend/dashboard/>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */

// Timeframe button constants
var BUTTON_COUNT_PREFIX = "count_";
var BUTTON_COUNT_DEFAULT = "builds";
var BUTTONS_COUNT = {
    "builds": {
        "caption": "Builds",
        "keenEventCollection": "build_jobs",
        "keenTargetProperty": "job.build"
    },
    "jobs": {
        "caption": "Build jobs",
        "keenEventCollection": "build_jobs",
        "keenTargetProperty": "job.job"
    },
    "substages": {
        "caption": "Stages",
        "keenEventCollection": "build_substages",
        "keenTargetProperty": "job.job"
    }
};

var countButtons = new ButtonClass(
    BUTTONS_COUNT,
    BUTTON_COUNT_DEFAULT,
    BUTTON_COUNT_PREFIX
);
countButtons.onClick = function() { updateCountCharts(); };

var queryBuildsPerProject, queryBuildsPerProjectPie, requestBuildsPerProject, requestBuildsPerProjectPie;

function initCharts() {
    // get Update Period settings
    var updatePeriod = getUpdatePeriod();

    // initialize timeframe buttons
    timeframeButtons.initButtons();
    countButtons.initButtons();

    var keenMaxAge = updatePeriod.keenMaxAge;
    var keenTimeframe = updatePeriod.keenTimeframe;
    var keenInterval = updatePeriod.keenInterval;

    // display charts
    $('#charts').show();

    // visualization code goes here
    Keen.ready(function() {
        /* Builds per project */
        // create query
        queryBuildsPerProject = new Keen.Query("count_unique", {
            eventCollection: "build_jobs",
            targetProperty: "job.build",
            groupBy: "buildtime_trend.project_name",
            interval: keenInterval,
            timeframe: keenTimeframe,
            maxAge: keenMaxAge,
            timezone: TIMEZONE_SECS
        });
        queriesTimeframe.push(queryBuildsPerProject);
        queriesInterval.push(queryBuildsPerProject);

        // draw chart
        var chartBuildsPerProject = new Keen.Dataviz()
            .el(document.getElementById("chart_builds_per_project"))
            .chartType("columnchart")
            .height(400)
            .attributes({
                chartOptions: {
                    isStacked: true
                }
            })
           .prepare();

        requestBuildsPerProject = client.run(queryBuildsPerProject, function(err, res) {
            if (err) {
                // Display the API error
                chartBuildsPerProject.error(err.message);
            } else {
                chartBuildsPerProject
                    .parseRequest(this)
                    .title("Builds per project")
                    .render();
            }
        });
        queryRequests.push(requestBuildsPerProject);

        /* Builds per project (piechart)*/
        // create query
        queryBuildsPerProjectPie = new Keen.Query("count_unique", {
            eventCollection: "build_jobs",
            targetProperty: "job.build",
            groupBy: "buildtime_trend.project_name",
            timeframe: keenTimeframe,
            maxAge: keenMaxAge,
            timezone: TIMEZONE_SECS
        });
        queriesTimeframe.push(queryBuildsPerProjectPie);

        // draw chart
        var chartBuildsPerProjectPie = new Keen.Dataviz()
            .el(document.getElementById("chart_builds_per_project_pie"))
            .height(400)
            .prepare();

        requestBuildsPerProjectPie = client.run(queryBuildsPerProjectPie, function(err, res) {
            if (err) {
                // Display the API error
                chartBuildsPerProjectPie.error(err.message);
            } else {
                chartBuildsPerProjectPie
                    .parseRequest(this)
                    .title("Builds per project")
                    .render();
            }
        });
        queryRequests.push(requestBuildsPerProjectPie);

    });
}

/**
 * Refresh count charts eventCollection and targetProperty selected by countButtons.
 */
function updateCountCharts() {
  // get Update Period settings
  var countSettings = countButtons.getCurrentButton();

  // update queries
  queryBuildsPerProject.set({
    eventCollection: countSettings.keenEventCollection,
    targetProperty: countSettings.keenTargetProperty
  });
  queryBuildsPerProjectPie.set({
    eventCollection: countSettings.keenEventCollection,
    targetProperty: countSettings.keenTargetProperty
  });

  // refresh all query requests
  requestBuildsPerProject.refresh();
  requestBuildsPerProjectPie.refresh();
}

// initialize page
$(document).ready(function() {
    updateTitle();
    initLinks();
    initMessage();
    populateProjects();
    if (!isEmpty(keenConfig.projectId) && !isEmpty(keenConfig.readKey)) {
        initCharts();
    }
});
