# DoP Mobile Navigation Matrix (Dar es Salaam Market Launch)

| Capability | Navigation | Destination |
|---|---|---|
| CUSTOMER | Home | CustomerHomeScreen |
| CUSTOMER | Explore | CustomerExploreScreen |
| CUSTOMER | Saved | CustomerSavedScreen |
| CUSTOMER | Visits | CustomerVisitsScreen |
| CUSTOMER | Account | UnifiedAccountCenterScreen |
| OWNER | Dashboard | OwnerDashboardScreen |
| OWNER | Properties | OwnerPropertiesScreen |
| OWNER | Add Property | OwnerPropertyStudioScreen |
| OWNER | Viewings | OwnerViewingsScreen |
| OWNER | Account | UnifiedAccountCenterScreen |
| GUIDE | Dashboard | GuideMainDashboardScreen |
| GUIDE | Jobs | GuideJobsScreen |
| GUIDE | Schedule | GuideScheduleScreen |
| GUIDE | Earnings | GuideEarningsScreen |
| GUIDE | Account | UnifiedAccountCenterScreen |

## Notes
- **Admin**: Accessible strictly via a 14-tap hidden gesture on the splash/login screen (`AdminTapDetector`). Does NOT appear in standard capability navigation.
- **Market Scope**: Set to `DAR_ES_SALAAM`. `MarketConfig` dictates active markets, preventing unauthorized expansion before backend/logistics support is ready.
