package javagh.jenkins.mashupportlets;

import hudson.Extension;
import hudson.model.Descriptor;
import hudson.plugins.view.dashboard.DashboardPortlet;
import hudson.util.ListBoxModel;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.HttpResponse;
import org.kohsuke.stapler.bind.JavaScriptMethod;

/**
 * Shows SonarQube issues in a portlet.
 * 
 * @author G.Henzler
 *
 */
public class SonarIssuesPortlet extends AbstractMashupPortlet {

    private static final int DEFAULT_PRIO_NO = -1;

    private final String sonarBaseUrl;
    private final String sonarProjectsList;

    private final String sonarFilter;
    private final int sonarPriorityThreshold;
    private final int sonarNewIssuesPriorityThreshold;

    private final int sonarAssigneeStatus;
    private final boolean sonarShowAssigneeBar;
    private final String labelAssigneesRanking;
    private final int maxAssigneesInRanking;
    
    private final int maxEntries;
    private final int deltaDaysForNewIssues;

    private final String divId;
    
    private final String sonarApiUser;
    private final String sonarApiPw;
    
    private final boolean showAlerts;
    private final String metricsCheckedForAlerts;
    private final String alwaysShowMetrics;
    
    
    

    @DataBoundConstructor
    public SonarIssuesPortlet(String name, String sonarBaseUrl,
            String sonarProjectsList, String sonarFilter, int sonarPriorityThreshold, int sonarAssigneeStatus, boolean sonarShowAssigneeBar,
            // alerts
            boolean showAlerts, String metricsCheckedForAlerts, String alwaysShowMetrics,
            // advanced
            int maxEntries, int sonarNewIssuesPriorityThreshold,
            int deltaDaysForNewIssues, int violationDescriptionMaximumLength, String sonarApiUser, 
            String sonarApiPw, String labelAssigneesRanking, int maxAssigneesInRanking) {
        super(name);
        

        this.sonarBaseUrl = Utils.normalizeBaseUrl(sonarBaseUrl);   

        this.sonarProjectsList = sonarProjectsList;
        this.sonarFilter = sonarFilter;

        this.sonarPriorityThreshold = sonarPriorityThreshold;

        this.sonarAssigneeStatus = sonarAssigneeStatus;
        this.sonarShowAssigneeBar = sonarShowAssigneeBar;
        this.labelAssigneesRanking = labelAssigneesRanking;
        this.maxAssigneesInRanking = maxAssigneesInRanking;
        
        this.maxEntries = maxEntries;

        this.sonarNewIssuesPriorityThreshold = sonarNewIssuesPriorityThreshold;

        this.deltaDaysForNewIssues = deltaDaysForNewIssues;

        this.sonarApiUser = sonarApiUser;
        this.sonarApiPw = sonarApiPw;
        
        this.showAlerts = showAlerts;
        this.metricsCheckedForAlerts = metricsCheckedForAlerts;
        this.alwaysShowMetrics = alwaysShowMetrics;
        
        this.divId = "sonarDiv_" + getId();
    }
    
    
    public String getDivId() {
        return divId;
    }

    public String getSonarBaseUrl() {
        return sonarBaseUrl;
    }

    public String getSonarProjectsList() {
        return sonarProjectsList;
    }
    
    public String getsonarFilter() {
    	return sonarFilter;
    }
    
    public int getMaxEntries() {
        return maxEntries > 0 ? maxEntries : 50;
    }
    
    public int getMaxAssigneesInRanking() {
        return maxAssigneesInRanking > 0 ? maxAssigneesInRanking : 5;
    }

    public int getSonarPriorityThreshold() {
        return sonarPriorityThreshold;
    }

    public int getSonarNewIssuesPriorityThreshold() {
        return sonarNewIssuesPriorityThreshold;
    }

    public int getDeltaDaysForNewIssues() {
        return deltaDaysForNewIssues > 0 ? deltaDaysForNewIssues : 5;
    }

    public int getSonarAssigneeStatus() {
		return sonarAssigneeStatus;
	}
	
	public boolean isSonarShowAssigneeBar() {
		return sonarShowAssigneeBar;
	}

	public String getLabelAssigneesRanking() {
		return labelAssigneesRanking;
	}


	public String getSonarApiUser() {
		return sonarApiUser;
	}

	public String getSonarApiPw() {
		return sonarApiPw;
	}

	public boolean getShowAlerts() {
		return showAlerts;
	}

	public String getMetricsCheckedForAlerts() {
		return metricsCheckedForAlerts;
	}

	public String getMetricsCheckedForAlertsJson() {
		String metricsCheckedForAlerts = StringUtils
				.isBlank(this.metricsCheckedForAlerts) ? 
						"complexity,class_complexity,file_complexity,function_complexity,file_cycles,file_edges_weight,package_tangles,file_tangles,file_tangle_index,"
						+ "package_cycles,package_feedback_edges,package_tangle_index,package_edges_weight,file_feedback_edges,comment_lines,comment_lines_density,"
						+ "public_documented_api_density,public_undocumented_api,duplicated_blocks,duplicated_files,duplicated_lines,duplicated_lines_density,new_violations,"
						+ "new_blocker_violations,new_critical_violations,new_major_violations,blocker_violations,critical_violations,major_violations,violations,"
						+ "false_positive_issues,open_issues,confirmed_issues,reopened_issues,weighted_violations,violations_density,sqale_index,branch_coverage,"
						+ "new_branch_coverage,coverage,new_coverage,new_line_coverage,skipped_tests,uncovered_conditions,new_uncovered_conditions,uncovered_lines,"
						+ "new_uncovered_lines,tests,test_execution_time,test_errors,test_failures,test_success_density"
				: this.metricsCheckedForAlerts;
		String metricsCheckedForAlertsJson = Utils
				.configListToJsonList(metricsCheckedForAlerts);
		return metricsCheckedForAlertsJson;
	}

	public String getAlwaysShowMetrics() {
		return alwaysShowMetrics;
	}
	public String getAlwaysShowMetricsJson() {
        String alwaysShowMetricsJson = Utils.configListToJsonList(alwaysShowMetrics);
        return alwaysShowMetricsJson;
    }

	public String getSonarProjectsJson() {
        String projectsJson = Utils.configListToJsonList(sonarProjectsList);
        return projectsJson;
    }

	public String getSonarFilterJson() {
        String filterJson = Utils.configListToJsonList(sonarFilter);
        return filterJson;
    }

    public String getSonarPrioritiesJson() {
        return getPrioritiesListForThreshold(sonarPriorityThreshold);
    }

    public String getSonarNewIssuesPrioritiesJson() {
        int applicableThreshold = sonarNewIssuesPriorityThreshold != DEFAULT_PRIO_NO ? sonarNewIssuesPriorityThreshold
                : sonarPriorityThreshold;
        return getPrioritiesListForThreshold(applicableThreshold);
    }

    private String getPrioritiesListForThreshold(int threshold) {
        SonarPriority[] values = SonarPriority.values();
        Object[] applicableValues = ArrayUtils.subarray(values, threshold, values.length);
        ArrayUtils.reverse(applicableValues);
        String json = "['" + StringUtils.join(applicableValues, "', '") + "']";
        return json;
    }

    public String getPriorityValueByNameJson() {
        return SonarPriority.getPriorityValueByNameJson();
    }
    public String getAssigneeStatusValueByNameJson() {
        return SonarAssigneeStatus.getPriorityValueByNameJson();
    }
    
    

	@JavaScriptMethod
    public HttpResponse ajaxViaJenkins(String urlStr) {
		return new ServerSideHttpCall(urlStr, sonarApiUser, sonarApiPw);
    }

    @Extension
    public static class SonarIssuesPortletDescriptor extends Descriptor<DashboardPortlet> {

        @Override
        public String getDisplayName() {
            return "SonarQube Issues";
        }

        public ListBoxModel doFillSonarPriorityThresholdItems() {
            ListBoxModel items = new ListBoxModel();
            for (SonarPriority sonarPriority : SonarPriority.values()) {
                items.add(sonarPriority.name(), String.valueOf(sonarPriority.ordinal()));
            }
            return items;
        }

        public ListBoxModel doFillSonarNewIssuesPriorityThresholdItems() {
            ListBoxModel items = new ListBoxModel();
            items.add("Same as for default list", String.valueOf(DEFAULT_PRIO_NO));
            items.addAll(doFillSonarPriorityThresholdItems());
            return items;
        }
        
        public ListBoxModel doFillSonarAssigneeStatusItems() {
            ListBoxModel items = new ListBoxModel();
            for (SonarAssigneeStatus sonarAssigneeStatus : SonarAssigneeStatus.values()) {
                items.add(sonarAssigneeStatus.name(), String.valueOf(sonarAssigneeStatus.ordinal()));
            }
            return items;
        }

    }

}
