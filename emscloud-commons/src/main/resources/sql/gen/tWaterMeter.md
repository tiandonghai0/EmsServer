sample
===
*

    select #use("cols")# from t_water_meter  where  #use("condition")#

cols
===
	f_id,f_project_no,F_water_meter_number,f_water_yield,F_create_time,F_update_time,F_create_user,F_update_user,f_last_open_time,f_last_close_time

updateSample
===

	f_id=#fId#,f_project_no=#fProjectNo#,F_water_meter_number=#fWaterMeterNumber#,f_water_yield=#fWaterYield#,F_create_time=#fCreateTime#,F_update_time=#fUpdateTime#,F_create_user=#fCreateUser#,F_update_user=#fUpdateUser#,f_last_open_time=#fLastOpenTime#,f_last_close_time=#fLastCloseTime#

condition
===

    1 = 1
    @if(!isEmpty(fId)){
     and f_id=#fId#
    @}
        @if(!isEmpty(fProjectNo)){
     and f_project_no=#fProjectNo#
    @}
        @if(!isEmpty(fWaterMeterNumber)){
     and F_water_meter_number=#fWaterMeterNumber#
    @}
        @if(!isEmpty(fWaterYield)){
     and f_water_yield=#fWaterYield#
    @}
        @if(!isEmpty(fCreateTime)){
     and F_create_time=#fCreateTime#
    @}
        @if(!isEmpty(fUpdateTime)){
     and F_update_time=#fUpdateTime#
    @}
        @if(!isEmpty(fCreateUser)){
     and F_create_user=#fCreateUser#
    @}
        @if(!isEmpty(fUpdateUser)){
     and F_update_user=#fUpdateUser#
    @}
        @if(!isEmpty(fLastOpenTime)){
     and f_last_open_time=#fLastOpenTime#
    @}
        @if(!isEmpty(fLastCloseTime)){
     and f_last_close_time=#fLastCloseTime#
    @}
    