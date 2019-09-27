function    [meas,svn2]   =   get2freqMeas(meas)
% Outputs:
%           Meas: Structure with mesures with 2freq; that is: as long as we
%           have L5 measurements we use them if not L1 measurements are
%           also included. 
%               - Meas.pr
%               - Meas.svn
%               - Meas.cn0
%           idx: vector with svn of satellites with both L1 and L5
%           measurements

    [svn2,~,i5]     =   intersect(meas.L1.svn,meas.L5.svn); 
    ttt             =   meas.L5.svn(i5);
    pr              =   meas.L5.pr(i5);
    CN0             =   meas.L5.cn0(i5);
    N2              =   length(ttt);
    if(N2 ~= length(meas.L5.svn))
        aa          =   setdiff(meas.L5.svn,meas.L1.svn);
        for ii =1:length(aa) 
            ttt     =   [ttt;aa(ii)];
            pr      =   [pr;meas.L5.pr(meas.L5.svn==aa(ii))];
            CN0     =   [CN0;meas.L5.cn0(meas.L5.svn==aa(ii))];
        end
        N2       =   length(ttt);
    end
    aa           =   setdiff(meas.L1.svn,meas.L5.svn);
    if(~isempty(aa))
       for ii = 1:length(aa)
           ttt          =   [ttt;aa(ii)];
           pr           =   [pr;meas.L1.pr(meas.L1.svn==aa(ii))];
           CN0          =   [CN0;meas.L1.cn0(meas.L1.svn==aa(ii))];
       end
       N1           =   length(aa);
%        eph_idx      =   [2*ones(N2,1);ones(N1,1)]; 
    else
%        eph_idx      =   2*ones(N2,1); 
    end
    meas.nsat   =   length(pr);
    meas.pr     =   pr;
    meas.svn    =   ttt;
    meas.cn0    =   CN0;
%     idx         =   id5;





end