selectByCondition
===
* 注释

	select #use("cols")# from t_sub_project  where  #use("condition")#

cols
===
	sub_project_id,project_id,station_id,sub_project_name

updateSample
===
	
	sub_project_id=#subProjectId#,project_id=#projectId#,station_id=#stationId#,sub_project_name=#subProjectName#

condition
===

	1 = 1  
	@if(!isEmpty(subProjectId)){
	 and sub_project_id=#subProjectId#
	@}
	@if(!isEmpty(projectId)){
	 and project_id=#projectId#
	@}
	@if(!isEmpty(stationId)){
	 and station_id=#stationId#
	@}
	@if(!isEmpty(subProjectName)){
	 and sub_project_name=#subProjectName#
	@}
	
	