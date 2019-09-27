function [gst_week, gst_sow, gst_dow] = date2gst(date)

%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
%
% The function converts the calendaristic date into Galileo System Time
% (GST)
%
% created by: Sebastian Ciuban
% company   : European Space Agency
%
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
%
% INPUT:
%       date        =  [year, month, day, hour, min, sec]]              [1 x 6]
%
% OUTPUT:
%
%       gst_week    = GST week number                                   [scalar]
%       gst_sow     = GST seconds of the week                           [scalar]
%       gst_dow     = GST day of the week                               [scalar]
%
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

% Start of the Galileo System Time (22 August 1999)
gst_start_datenum = 730354; %This is datenum([1999,8,22,0,0,0])

%number of days since the beginning of Galileo System Time
deltat   = (datenum([date(:,1), date(:,2), date(:,3)]) - gst_start_datenum);

gst_week = floor(deltat/7);                                    %G ST week number
gst_dow  = floor(deltat - gst_week*7);                         % GST day of week
gst_sow  = (deltat - gst_week*7)*86400; 
gst_sow = gst_sow + date(:,4)*3600 + date(:,5)*60 + date(:,6); % GST seconds of week