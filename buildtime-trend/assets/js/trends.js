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

var TIMEFRAME_LAST_WEEK = "this_7_days";
var TIMEFRAME_LAST_MONTH = "this_30_days";
var TIMEFRAME_LAST_YEAR = "this_52_weeks";

var CAPTION_LAST_WEEK = "Last week";
var CAPTION_LAST_MONTH = "Last month";
var CAPTION_LAST_YEAR = "Last year";

var TIMEZONE_SECS = "UTC"; // named timezone or offset in seconds, fe. GMT+1 = 3600

// arrays with queries and query request to update
var queriesInterval = [];
var queriesTimeframe = [];
var queryRequests = [];

/**
 * Checks if a variable is defined and not null.
 */
function isEmpty(varName) {
   return (varName === undefined || varName === null);
}

/**
 * Escape html characters
 * inspired by http://css-tricks.com/snippets/javascript/htmlentities-for-javascript/
 */
function htmlEntities(str) {
    return String(str).replace(/&/g, '&amp;').replace(/</g, '&lt;').replace(/>/g, '&gt;').replace(/"/g, '&quot;').replace(/'/g, '&#039;');
}

/**
 * Merge data from several series, with identical X-axis labels
 *
 * Parameters :
 * - data : Keen.io query results
 * - indexCaptions : Captions for the index of the values
 * - valueFieldname : name of the value field in the query result array
 * - seriesCaptions : captions for the data series
 */
function mergeSeries(data, indexCaptions, valueFieldname, seriesCaptions) {
    var chartData = [];
    // create and populate data array
    var i, j;
    for (i = 0; i < indexCaptions.length; i++) {
        chartData[i]={caption: indexCaptions[i]};
        // populate all series
        for (j = 0; j < seriesCaptions.length; j++) {
            chartData[i][seriesCaptions[j]] = 0;
        }
    }
    // loop over all query result sets
    for (j = 0; j < data.length; j++) {
        var timeframeResult = data[j].result;
        var timeframeCaption = seriesCaptions[j];
        // copy query data into the populated array
        for (i = 0; i < timeframeResult.length; i++) {
            var index = parseInt(timeframeResult[i][valueFieldname], 10);
            chartData[index][timeframeCaption] = timeframeResult[i].result;
        }
    }

    return chartData;
}

function getUpdatePeriod(period) {
    var keenTimeframe, keenInterval;

    switch (period) {
    case "day":
        keenTimeframe = "today";
        keenInterval = "hourly";
        break;
    default:
        period = "week";
    case "week":
        keenTimeframe = TIMEFRAME_LAST_WEEK;
        keenInterval = "daily";
        break;
    case "month":
        keenTimeframe = TIMEFRAME_LAST_MONTH;
        keenInterval = "daily";
        break;
    case "year":
        keenTimeframe = TIMEFRAME_LAST_YEAR;
        keenInterval = "weekly";
        break;
    }

    return {
        name: period,
        keenTimeframe: keenTimeframe,
        keenInterval: keenInterval
    };
}

// Get badge url
function getBadgeUrl() {
    // check if config.serviceUrl is set by something else than the default value
    if (isEmpty(config.serviceUrl) || config.serviceUrl === 'service_url') {
        config.serviceUrl = 'https://buildtimetrend.herokuapp.com/';
    }

    return config.serviceUrl + '/badge/';
}

// Initialize badge url
function updateBadgeUrl(periodName) {
    var badgeUrl = getBadgeUrl();

    // add repo
    if (!isEmpty(config.repoName) && config.repoName !== 'repo_name') {
        badgeUrl += config.repoName;

        var updatePeriod = getUpdatePeriod(periodName);
        var interval = updatePeriod.name;

        // add interval
        if (isEmpty(interval) || interval === 'day') {
            badgeUrl += '/latest';
        } else {
            badgeUrl += '/avg/' + interval;
        }
    }

    // change badge url
    $("#badge-url").attr('src', htmlEntities(badgeUrl));
}

function updateCharts(periodName) {
    // get Update Period settings
    var updatePeriod = getUpdatePeriod(periodName);

    updateBadgeUrl(updatePeriod.name);

    var i;

    // update all interval based queries
    for (i = 0; i < queriesInterval.length; i++) {
        queriesInterval[i].set({interval: updatePeriod.keenInterval});
    }

    // update all timeframe based queries
    for (i = 0; i < queriesTimeframe.length; i++) {
        queriesTimeframe[i].set({timeframe: updatePeriod.keenTimeframe});
    }

    // refresh all updated query requests
    for (i = 0; i < queryRequests.length; i++) {
        queryRequests[i].refresh();
    }
}

function initCharts() {
    // get Update Period settings
    var updatePeriod = getUpdatePeriod();

    var keenTimeframe = updatePeriod.keenTimeframe;
    var keenInterval = updatePeriod.keenInterval;

    // update interval selection box
    $('#intervals').val(updatePeriod.name);

    // display charts
    $('#charts').show();

    // visualization code goes here
    Keen.ready(function() {
        /* Total builds */
        // create query
        var queryTotalBuilds = new Keen.Query("count", {
            eventCollection: "build_jobs",
            timezone: TIMEZONE_SECS,
            timeframe: keenTimeframe
        });
        queriesTimeframe.push(queryTotalBuilds);

        // draw chart
        var requestTotalBuilds = client.run(queryTotalBuilds, function() {
            this.draw(document.getElementById("metric_total_builds"), {
                title: "Total build jobs", width: "200"
            });
        });
        queryRequests.push(requestTotalBuilds);

        /* Total builds passed */
        // create query
        var queryTotalBuildsPassed = new Keen.Query("count", {
            eventCollection: "build_jobs",
            timezone: TIMEZONE_SECS,
            timeframe: keenTimeframe,
            filters: [{"property_name":"job.result","operator":"eq","property_value":"passed"}]
        });
        queriesTimeframe.push(queryTotalBuildsPassed);

        // combine queries for conditional coloring of TotalBuildspassed
        var colorBuildsPassed = client.run([queryTotalBuilds, queryTotalBuildsPassed], function(result){
            var chartColor = ["green"];
            var totalBuilds = result[0].result;
            var totalBuildsPassed = result[1].result;

            if (totalBuilds === totalBuildsPassed) {
                chartColor = ["green"];
            } else if (totalBuilds > 0) {
                if ((totalBuildsPassed / totalBuilds) >= 0.75) {
                    chartColor = ["orange"];
                } else {
                    chartColor = ["red"];
                }
            }

            // draw chart
            client.draw(queryTotalBuildsPassed, document.getElementById("metric_total_builds_passed"),
                {
                    title: "Build jobs passed",
                    colors: chartColor,
                    width: "200"
                }
            );
        });
        queryRequests.push(colorBuildsPassed);

        /* Total builds passed */
        // create query
        var queryTotalBuildsFailed = new Keen.Query("count", {
            eventCollection: "build_jobs",
            timezone: TIMEZONE_SECS,
            timeframe: keenTimeframe,
            filters: [{"property_name":"job.result","operator":"in","property_value":["failed","errored"]}]
        });
        queriesTimeframe.push(queryTotalBuildsFailed);

        // combine queries for conditional coloring of TotalBuildsfailed
        var colorBuildsFailed = client.run([queryTotalBuilds, queryTotalBuildsFailed], function(result){
            var chartColor = ["green"];
            var totalBuilds = result[0].result;
            var totalBuildsFailed = result[1].result;

            if (totalBuildsFailed === 0) {
                chartColor = ["green"];
            } else if (totalBuilds > 0) {
                if ((totalBuildsFailed / totalBuilds) <= 0.25) {
                    chartColor = ["orange"];
                } else {
                    chartColor = ["red"];
                }
            }

            // draw chart
            client.draw(queryTotalBuildsFailed, document.getElementById("metric_total_builds_failed"),
                {
                    title: "Build jobs failed",
                    colors: chartColor,
                    width: "200"
                }
            );
        });
        queryRequests.push(colorBuildsFailed);

        /* average build time of all stages */
        // create query
        var queryAverageBuildTime = new Keen.Query("average", {
            eventCollection: "build_jobs",
            timezone: TIMEZONE_SECS,
            timeframe: keenTimeframe,
            targetProperty: "job.duration"
        });
        queriesTimeframe.push(queryAverageBuildTime);

        // draw chart
        var requestAverageBuildTime = client.run(queryAverageBuildTime, function() {
            this.draw(document.getElementById("metric_average_build_time"), {
                title: "Average job duration",
                width: "250",
                chartOptions: {
                    suffix: "s"
                }
            });
        });
        queryRequests.push(requestAverageBuildTime);

        /* average stage duration */
        // create query
        var queryStageDuration = new Keen.Query("average", {
            eventCollection: "build_stages",
            timezone: TIMEZONE_SECS,
            timeframe: keenTimeframe,
            interval: keenInterval,
            targetProperty: "stage.duration",
            groupBy: "stage.name",
            filters: [{"property_name":"stage.name","operator":"exists","property_value":true}]
        });
        queriesTimeframe.push(queryStageDuration);
        queriesInterval.push(queryStageDuration);

        // draw chart
        var requestStageDuration = client.run(queryStageDuration, function() {
            this.draw(document.getElementById("chart_stage_duration"), {
                chartType: "columnchart",
                title: "Average build stage duration",
                chartOptions: {
                    isStacked: true,
                    vAxis: {title: "duration [s]"}
                }
            });
        });
        queryRequests.push(requestStageDuration);

        /* Stage duration fraction */
        // create query
        var queryStageFraction = new Keen.Query("average", {
            eventCollection: "build_stages",
            timezone: TIMEZONE_SECS,
            timeframe: keenTimeframe,
            targetProperty: "stage.duration",
            groupBy: "stage.name",
            filters: [{"property_name":"stage.name","operator":"exists","property_value":true}]
        });
        queriesTimeframe.push(queryStageFraction);

        // draw chart
        var requestStageFraction = client.run(queryStageFraction, function() {
            this.draw(document.getElementById("chart_stage_fraction"), {
                title: "Build stage fraction of total build duration"
            });
        });
        queryRequests.push(requestStageFraction);

        /* Builds */
        // create query
        var queryBuilds = new Keen.Query("count_unique", {
            eventCollection: "build_jobs",
            timezone: TIMEZONE_SECS,
            timeframe: keenTimeframe,
            interval: keenInterval,
            targetProperty: "job.build",
            groupBy: "job.branch"
        });
        queriesTimeframe.push(queryBuilds);
        queriesInterval.push(queryBuilds);

        // draw chart
        var requestBuilds = client.run(queryBuilds, function() {
            this.draw(document.getElementById("chart_builds"), {
                chartType: "columnchart",
                title: "Builds per branch",
                chartOptions: {
                    isStacked: true,
                    vAxis: {title: "build count"}
                }
            });
        });
        queryRequests.push(requestBuilds);

        /* Builds per branch */
        // create query
        var queryTotalBuildsBranch = new Keen.Query("count_unique", {
            eventCollection: "build_jobs",
            timezone: TIMEZONE_SECS,
            timeframe: keenTimeframe,
            targetProperty: "job.build",
            groupBy: "job.branch"
        });
        queriesTimeframe.push(queryTotalBuildsBranch);

        // draw chart
        var requestTotalBuildsBranch = client.run(queryTotalBuildsBranch, function() {
            this.draw(document.getElementById("chart_total_builds_branch"), {
                title: "Builds per branch (%)"
            });
        });
        queryRequests.push(requestTotalBuildsBranch);

        /* Average buildtime per time of day */
        // create query
        var queryAvgBuildtimeHourLastWeek = new Keen.Query("average", {
            eventCollection: "build_jobs",
            timezone: TIMEZONE_SECS,
            timeframe: TIMEFRAME_LAST_WEEK,
            targetProperty: "job.duration",
            groupBy: "job.started_at.hour_24",
            filters: [{"property_name":"job.started_at.hour_24","operator":"exists","property_value":true}]
        });
        var queryAvgBuildtimeHourLastMonth = new Keen.Query("average", {
            eventCollection: "build_jobs",
            timezone: TIMEZONE_SECS,
            timeframe: TIMEFRAME_LAST_MONTH,
            targetProperty: "job.duration",
            groupBy: "job.started_at.hour_24",
            filters: [{"property_name":"job.started_at.hour_24","operator":"exists","property_value":true}]
        });
        var queryAvgBuildtimeHourLastYear = new Keen.Query("average", {
            eventCollection: "build_jobs",
            timezone: TIMEZONE_SECS,
            timeframe: TIMEFRAME_LAST_YEAR,
            targetProperty: "job.duration",
            groupBy: "job.started_at.hour_24",
            filters: [{"property_name":"job.started_at.hour_24","operator":"exists","property_value":true}]
        });

        // generate chart
        var requestAvgBuildtimeHour = client.run(
                [queryAvgBuildtimeHourLastWeek,
                    queryAvgBuildtimeHourLastMonth,
                    queryAvgBuildtimeHourLastYear],
                function()
        {
            var timeframeCaptions = [CAPTION_LAST_WEEK, CAPTION_LAST_MONTH, CAPTION_LAST_YEAR];
            var indexCaptions = [];
            
            // populate array with an entry per hour
            var i;
            for (i = 0; i < 24; i++) {
                indexCaptions[i]= String(i) + ":00";
            }

            var chartData = mergeSeries(
                this.data,
                indexCaptions,
                "job.started_at.hour_24",
                timeframeCaptions
            );

            // draw chart
            window.chart = new Keen.Visualization(
                {result: chartData},
                document.getElementById("chart_avg_buildtime_hour"),
                {
                    chartType: "columnchart",
                    title: "Average buildtime per time of day",
                    chartOptions: {
                    vAxis: { title: "duration [s]" },
                    hAxis: {
                        title: "Time of day [24-hour format, UTC]",
                        slantedText: "true",
                        slantedTextAngle: "90"
                    }
                }
            });
        });
        queryRequests.push(requestAvgBuildtimeHour);

        /* Average buildtime per day of week */
        // create query
        var queryAvgBuildtimeWeekDayLastWeek = new Keen.Query("average", {
            eventCollection: "build_jobs",
            timezone: TIMEZONE_SECS,
            timeframe: TIMEFRAME_LAST_WEEK,
            targetProperty: "job.duration",
            groupBy: "job.started_at.day_of_week",
            filters: [
                {
                    "property_name":"job.started_at.day_of_week",
                    "operator":"exists",
                    "property_value":true
                }
            ]
        });
        var queryAvgBuildtimeWeekDayLastMonth = new Keen.Query("average", {
            eventCollection: "build_jobs",
            timezone: TIMEZONE_SECS,
            timeframe: TIMEFRAME_LAST_MONTH,
            targetProperty: "job.duration",
            groupBy: "job.started_at.day_of_week",
            filters: [
                {
                    "property_name":"job.started_at.day_of_week",
                    "operator":"exists",
                    "property_value":true
                }
            ]
        });
        var queryAvgBuildtimeWeekDayLastYear = new Keen.Query("average", {
            eventCollection: "build_jobs",
            timezone: TIMEZONE_SECS,
            timeframe: TIMEFRAME_LAST_YEAR,
            targetProperty: "job.duration",
            groupBy: "job.started_at.day_of_week",
            filters: [
                {
                    "property_name":"job.started_at.day_of_week",
                    "operator":"exists",
                    "property_value":true
                }
            ]
        });

        // generate chart
        var requestAvgBuildtimeWeekDay = client.run(
                [queryAvgBuildtimeWeekDayLastWeek,
                    queryAvgBuildtimeWeekDayLastMonth,
                    queryAvgBuildtimeWeekDayLastYear],
                function()
        {
            var timeframeCaptions = [CAPTION_LAST_WEEK, CAPTION_LAST_MONTH, CAPTION_LAST_YEAR];
            var indexCaptions = ["Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat"];
            var chartData = mergeSeries(
                this.data,
                indexCaptions,
                "job.started_at.day_of_week",
                timeframeCaptions
            );

            // draw chart
            window.chart = new Keen.Visualization(
                {result: chartData},
                document.getElementById("chart_avg_buildtime_weekday"),
                {
                    chartType: "columnchart",
                    title: "Average buildtime per day of week",
                    chartOptions: {
                        vAxis: { title: "duration [s]" },
                        hAxis: { title: "Day of week" }
                }
            });
        });
        queryRequests.push(requestAvgBuildtimeWeekDay);
    });
}

// add project name to title
function updateTitle() {
    var title = 'Buildtime Trend as a Service';

    // check if config.projectName is set
    if (!isEmpty(config.projectName) && config.projectName !== 'project_name') {
        title = htmlEntities(config.projectName);
    } else if (!isEmpty(config.repoName) && config.repoName !== 'repo_name') {
        title = htmlEntities(config.repoName);
    }

    document.getElementById("title").innerHTML = title;
    document.getElementsByTagName("title")[0].innerHTML = "Buildtime Trend - " + title;
}

// Initialize link urls
function initLinks() {
    // check if config.serviceUrl is set by something else than the default value
    if (!isEmpty(config.websiteUrl) && config.websiteUrl !== 'website_url') {
        $("#title").attr('href', htmlEntities(config.websiteUrl));
    }

    // link to project repo and display icon
    if (!isEmpty(config.repoName) && config.repoName !== 'repo_name') {
        var repoUrl = "https://github.com/" + config.repoName;
        $("#repo-url").attr('href', htmlEntities(repoUrl));
        $("#repo-url").show();
    } else {
        // hide repo icon
        $("#repo-url").hide();
    }
}

// Display message
function initMessage() {
    // add message and display it
    if (!isEmpty(config.message)) {
        $("#message").append(htmlEntities(config.message));
        $("#message").show();
    } else {
        // hide message
        $("#message").hide();
    }
}

// Populate project menu
function populateProjects() {
    // check if config.projectList is defined
    if (!isEmpty(config.projectList) &&
      $.isArray(config.projectList) && config.projectList.length > 0) {
        var i;
        var projectRepo, projectUrl, badgeUrl, projectLinkDropdown, projectLinkOverview;

        for (i = 0; i < config.projectList.length; i++) {
            projectRepo = htmlEntities(config.projectList[i]);
            projectUrl = "/dashboard/" + projectRepo;
            badgeUrl = getBadgeUrl() + projectRepo;

            // add project link to dropdown menu
            projectLinkDropdown = '<li><a href="' + projectUrl + '">' +
                projectRepo + '</a></li>';
            $("#projects.dropdown ul").append(projectLinkDropdown);

            // add project link to project overview
            projectLinkOverview = '<li class="list-group-item">' +
                '<h4 class="list-group-item-heading">' + projectRepo + '</h4>' +
                '<a role="button" class="btn btn-primary" href="' +
                projectUrl + '">Dashboard</a>' +
                ' <a href="' + projectUrl + '"><img id="badge-url" src="' +
                    badgeUrl + '/latest" alt="Latest Buildtime" /></a>' +
                ' <a href="' + projectUrl + '"><img id="badge-url" src="' +
                    badgeUrl + '/builds" alt="Total Builds" /></a>' +
                ' <a href="' + projectUrl + '"><img id="badge-url" src="' +
                    badgeUrl + '/passed" alt="Successful builds" /></a>' +
                '</li>';
            $("#project-overview").append(projectLinkOverview);
        }

        // show projects dropdown menu
        $("#projects.dropdown").show();
    } else {
        // hide projects dropdown menu
        $("#projects.dropdown").hide();
    }
}

// initialize page
$(document).ready(function() {
    updateTitle();
    initLinks();
    initMessage();
    updateBadgeUrl();
    populateProjects();
    if (!isEmpty(config.repoName) &&
      !isEmpty(keenConfig.projectId) && !isEmpty(keenConfig.readKey)) {
        initCharts();
    }
});
