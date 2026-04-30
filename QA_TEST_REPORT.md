# QA Test Report

**Testing & QA Engineer:** Mbulaate Dison Wilson
**Date:** 30th April 2026
**Project:** Lost & Found Hub - Ndejje Campus Connect

---

## Test Cases vs Results

| Test ID | Test Case | Expected Result | Actual Result | Status |
|---------|-----------|----------------|---------------|--------|
| TC-01 | Create new Item | Item saved to Firebase | Item saved successfully | ✅ PASS |
| TC-02 | Upload image to ImgBB | Image URL returned | URL received | ✅ PASS |
| TC-03 | Login with valid credentials | User authenticated | Login successful | ✅ PASS |
| TC-04 | Login with invalid email | Error message displayed | Error shown | ✅ PASS |
| TC-05 | Sign up with new email | Account created | Registration successful | ✅ PASS |
| TC-06 | Filter Lost items | Only Lost items shown | Filter works | ✅ PASS |
| TC-07 | Filter Found items | Only Found items shown | Filter works | ✅ PASS |
| TC-08 | Add item with empty title | Validation error | "Title required" shown | ✅ PASS |
| TC-09 | Edit own item | Item updated | Update successful | ✅ PASS |
| TC-10 | Edit someone else's item | Access denied | Permission denied | ✅ PASS |
| TC-11 | Send chat message | Message delivered | Delivered successfully | ✅ PASS |
| TC-12 | Dark mode toggle | UI changes to dark theme | Dark mode works | ✅ PASS |
| TC-13 | Logout | Return to login screen | Logout successful | ✅ PASS |

---

## Test Summary

| Category | Total | Passed | Failed | Pass Rate |
|----------|-------|--------|--------|-----------|
| Authentication | 4 | 4 | 0 | 100% |
| Item Management | 5 | 5 | 0 | 100% |
| Chat System | 1 | 1 | 0 | 100% |
| UI/UX | 2 | 2 | 0 | 100% |
| Validation | 1 | 1 | 0 | 100% |
| **TOTAL** | **13** | **13** | **0** | **100%** |

---

## Test Environment

| Component | Details |
|-----------|---------|
| **Device** | Pixel 6 API 34 Emulator |
| **Android Version** | 14 (API 34) |
| **Test Framework** | JUnit4 + AndroidX Test |

---

## QA Sign-off

All tests have passed. Application is ready for production release.

**Signature:** Mbulaate Dison Wilson
**Date:** 30th April 2026
