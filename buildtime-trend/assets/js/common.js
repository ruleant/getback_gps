/* vim: set expandtab sw=4 ts=4: */
/**
 * Common functions.
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

/**
 * Checks if a variable is defined and not null.
 */
function isEmpty(varName) {
   return (varName === undefined || varName === null || varName === "");
}

/**
 * Escape html characters
 * inspired by http://css-tricks.com/snippets/javascript/htmlentities-for-javascript/
 */
function htmlEntities(str) {
    return String(str).replace(/&/g, '&amp;').replace(/</g, '&lt;').replace(/>/g, '&gt;').replace(/"/g, '&quot;').replace(/'/g, '&#039;');
}

/**
 * Get URL parameter
 * Inspired by http://stackoverflow.com/a/21903119
 */
function getUrlParameter(sParam) {
    var sPageURL = decodeURIComponent(window.location.search.substring(1)),
        sURLVariables = sPageURL.split('&'),
        sParameterName,
        i;

    for (i = 0; i < sURLVariables.length; i++) {
        sParameterName = sURLVariables[i].split('=');

        if (sParameterName[0] === sParam) {
            return sParameterName[1];
        }
    }
}

/**
 * Capitalize first character of a string
 */
function firstCharUpperCase(str) {
    return str.substring(0,1).toUpperCase() + str.substring(1);
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

// Get badge url
function getBadgeUrl() {
    // check if config.serviceUrl is set by something else than the default value
    if (isEmpty(config.serviceUrl) || config.serviceUrl === 'service_url') {
        config.serviceUrl = 'https://buildtimetrend.herokuapp.com/';
    }
    return config.serviceUrl + '/badge/';
}
