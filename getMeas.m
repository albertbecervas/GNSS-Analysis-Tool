function [pr,svn,CN0]     =   getMeas(meas)

%     Nsvn        =   length(meas);
    pr          =   [meas.pR]';
    svn         =   [meas.svid]';
    CN0         =   [meas.cn0]';
    
end