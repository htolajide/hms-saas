export const getHospitalId = () => {
  const id = localStorage.getItem('hospitalId');
  if (!id) {
    console.error("No hospitalId found in localStorage. User may not be properly authenticated.");
    return null;
  }
  return parseInt(id, 10);
};