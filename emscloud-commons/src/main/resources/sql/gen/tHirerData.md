sample
===
* 注释

	select #use("cols")# from t_hirer_data  where  #use("condition")#

cols
===
	id,user_name,area,unit,room_no,telephone,addr,room_area,project_id,valve_no,create_date,update_date,create_by,update_by

updateSample
===
	
	id=#id#,user_name=#userName#,area=#area#,unit=#unit#,room_no=#roomNo#,telephone=#telephone#,addr=#addr#,room_area=#roomArea#,project_id=#projectId#,valve_no=#valveNo#,create_date=#createDate#,update_date=#updateDate#,create_by=#createBy#,update_by=#updateBy#

condition
===

	1 = 1  
	@if(!isEmpty(id)){
	 and id=#id#
	@}
	@if(!isEmpty(userName)){
	 and user_name=#userName#
	@}
	@if(!isEmpty(area)){
	 and area=#area#
	@}
	@if(!isEmpty(unit)){
	 and unit=#unit#
	@}
	@if(!isEmpty(roomNo)){
	 and room_no=#roomNo#
	@}
	@if(!isEmpty(telephone)){
	 and telephone=#telephone#
	@}
	@if(!isEmpty(addr)){
	 and addr=#addr#
	@}
	@if(!isEmpty(roomArea)){
	 and room_area=#roomArea#
	@}
	@if(!isEmpty(projectId)){
	 and project_id=#projectId#
	@}
	@if(!isEmpty(valveNo)){
	 and valve_no=#valveNo#
	@}
	@if(!isEmpty(createDate)){
	 and create_date=#createDate#
	@}
	@if(!isEmpty(updateDate)){
	 and update_date=#updateDate#
	@}
	@if(!isEmpty(createBy)){
	 and create_by=#createBy#
	@}
	@if(!isEmpty(updateBy)){
	 and update_by=#updateBy#
	@}
	
	