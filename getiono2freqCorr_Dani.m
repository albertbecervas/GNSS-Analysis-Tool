function    [iono,i1]    =   getiono2freqCorr_Dani(L1,L5)

% Inputs:
%       L1: Band to correct (i.e. iono error is computed to correct L1
%       band)
%       L2: Secondary band
%       i2: svn of satellites with both L1 and L1 signals
% Outputs:
%       iono:
%       i1: idx for L1 band with common L1/L5 sats

    f1      =   L1.f;
    f2      =   L5.f;
    fact    =   (f2^2)/((f1^2)-(f2^2));
    %
    svn1    =   L1.svn;
    svn2    =   L5.svn;
    [~,i1,i2]   =   intersect(svn1,svn2);
    %
    pr1         =   L1.pr(i1);
    pr2         =   L5.pr(i2);
    %
    iono        =   fact*(pr2 - pr1);

end
