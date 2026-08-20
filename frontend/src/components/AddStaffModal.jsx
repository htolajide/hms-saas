import { useState, useEffect } from 'react';
import api from '../services/api';
import { X, Loader2, Users } from 'lucide-react';

export default function AddStaffModal({ onClose, onSaved, editData = null }) {
  const isEditing = !!editData;

  // 1. Initialize state
  const [formData, setFormData] = useState({
    hospitalId: 1,
    staffId: '',
    fullName: '',
    email: '',
    password: '',
    roleId: 3,
    rankId: 1, // NEW
    departmentId: 1,
    phone: '',
    qualification: ''
  });
  
  const [photoFile, setPhotoFile] = useState(null);
  const [photoPreview, setPhotoPreview] = useState(null);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');

  // 2. Watch for changes in editData to pre-fill the form
  useEffect(() => {
    if (editData) {
      setFormData({
        hospitalId: editData.hospitalId || 1,
        staffId: editData.staffId || '',
        fullName: editData.fullName || '',
        email: editData.email || '',
        password: '', // Always empty for editing
        roleId: editData.roleId || 3,
        rankId: editData.rankId || 1,
        departmentId: editData.departmentId || 1,
        phone: editData.phone || '',
        qualification: editData.qualification || ''
      });
      if (editData.passportPhoto) {
        setPhotoPreview(`http://localhost:8081/uploads/${editData.passportPhoto}`);
      } else {
        setPhotoPreview(null);
      }
    } else {
      resetForm();
    }
  }, [editData]);

  const [success, setSuccess] = useState(''); // <-- ADD THIS FOR SUCCESS MESSAGE

  const resetForm = () => {
    setFormData({
      hospitalId: 1, staffId: '', fullName: '', email: '', password: '',
      roleId: 3, rankId: 1, departmentId: 1, phone: '', qualification: '' // <-- ENSURE THESE ARE 1
    });
    setPhotoFile(null);
    setPhotoPreview(null);
    setError('');
    setSuccess('');
  };

  // 3. Handle text inputs
  const handleChange = (e) => {
    const { name, value } = e.target;
    setFormData(prev => ({ 
      ...prev, 
      [name]: name === 'basicSalary' || name === 'roleId' || name === 'hospitalId' ? Number(value) : value 
    }));
  };

  // 4. Handle file upload
  const handleFileChange = (e) => {
    const file = e.target.files[0];
    if (file) {
      setPhotoFile(file);
      setPhotoPreview(URL.createObjectURL(file));
    }
  };

  // 5. Handle form submission
  const handleSubmit = async (e) => {
    e.preventDefault();
    setLoading(true);
    setError('');
    setSuccess(''); 

    try {
      const formDataObj = new FormData();
      const staffData = { ...formData };
      
      // If editing and password is empty, remove it so backend doesn't overwrite it
      if (isEditing && !staffData.password) {
        delete staffData.password;
      }

      // Append the JSON data as a blob
      formDataObj.append('staff', new Blob([JSON.stringify(staffData)], { type: 'application/json' }));
      
      // Append the photo ONLY if a new file was actually selected
      if (photoFile) {
        formDataObj.append('photo', photoFile);
      }

      const url = isEditing ? `/staff/${editData.id}` : '/staff';
      const method = isEditing ? 'put' : 'post';

      // Axios interceptor will handle the headers automatically
      await api[method](url, formDataObj);
      
      // 1. Refresh the list in the background
      onSaved(); 
      
      // 2. Show success message
      setSuccess(isEditing ? 'Staff member updated successfully!' : 'New staff member added successfully!');
      
      // 3. Wait 1.5 seconds so the user can read the message, then close
      setTimeout(() => {
        onClose();
      }, 3000);

    } catch (err) {
      console.error("Save error details:", err.response?.data);
      setError(err.response?.data?.error || err.response?.data?.message || 'Failed to save staff member');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50 p-4">
      <div className="bg-white rounded-xl shadow-2xl w-full max-w-2xl max-h-[90vh] overflow-y-auto">
        
        {/* Modal Header */}
        <div className="flex justify-between items-center p-6 border-b border-gray-200 sticky top-0 bg-white z-10">
          <h3 className="text-xl font-bold text-gray-800">
            {isEditing ? 'Edit Staff Member' : 'Add New Staff Member'}
          </h3>
          <button onClick={onClose} className="text-gray-400 hover:text-gray-600 transition">
            <X className="h-6 w-6" />
          </button>
        </div>

        {/* Modal Body */}
        <form onSubmit={handleSubmit} className="p-6 space-y-4">

          {/* ADD THE SUCCESS MESSAGE HERE */}
          {success && (
            <div className="bg-green-50 text-green-700 p-3 rounded-lg text-sm mb-4 text-center font-medium border border-green-200 flex items-center justify-center gap-2">
              <svg className="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path strokeLinecap="round" strokeLinejoin="round" strokeWidth="2" d="M5 13l4 4L19 7"></path></svg>
              {success}
            </div>
          )}
          
          {error && <div className="bg-red-50 text-red-600 p-3 rounded-lg text-sm">{error}</div>}
          
          <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
            {/* Photo Upload Section */}
            <div className="md:col-span-2 flex items-center gap-4 p-4 bg-gray-50 rounded-lg border border-gray-200">
              <div className="shrink-0">
                {photoPreview ? (
                  <img src={photoPreview} alt="Preview" className="h-16 w-16 rounded-full object-cover border-2 border-white shadow-sm" />
                ) : (
                  <div className="h-16 w-16 rounded-full bg-gray-200 flex items-center justify-center text-gray-400">
                    <Users className="h-8 w-8" />
                  </div>
                )}
              </div>
              <div className="flex-grow">
                <label className="block text-sm font-medium text-gray-700 mb-1">Passport Photo</label>
                <input 
                  type="file" 
                  accept="image/*" 
                  onChange={handleFileChange} 
                  className="block w-full text-sm text-gray-500 file:mr-4 file:py-2 file:px-4 file:rounded-full file:border-0 file:text-sm file:font-semibold file:bg-blue-50 file:text-primary hover:file:bg-blue-100" 
                />
              </div>
            </div>

            {/* Form Fields */}
            <div>
              <label className="block text-sm font-medium text-gray-700 mb-1">Staff ID</label>
              <input name="staffId" value={formData.staffId} onChange={handleChange} required className="w-full px-3 py-2 bg-white text-gray-900 border border-gray-300 rounded-lg focus:ring-2 focus:ring-primary outline-none" />
            </div>
            <div>
              <label className="block text-sm font-medium text-gray-700 mb-1">Full Name</label>
              <input name="fullName" value={formData.fullName} onChange={handleChange} required className="w-full px-3 py-2 bg-white text-gray-900 border border-gray-300 rounded-lg focus:ring-2 focus:ring-primary outline-none" />
            </div>
            <div>
              <label className="block text-sm font-medium text-gray-700 mb-1">Email</label>
              <input name="email" type="email" value={formData.email} onChange={handleChange} required className="w-full px-3 py-2 bg-white text-gray-900 border border-gray-300 rounded-lg focus:ring-2 focus:ring-primary outline-none" />
            </div>
            <div>
              <label className="block text-sm font-medium text-gray-700 mb-1">
                Password {isEditing && <span className="text-xs text-gray-500 font-normal">(Leave blank to keep current)</span>}
              </label>
              <input 
                name="password" 
                type="password" 
                value={formData.password} 
                onChange={handleChange} 
                autoComplete={isEditing ? "off" : "new-password"}
                className="w-full px-3 py-2 bg-white text-gray-900 border border-gray-300 rounded-lg focus:ring-2 focus:ring-primary outline-none" 
              />
            </div>
            <div>
              <label className="block text-sm font-medium text-gray-700 mb-1">Department</label>
              <select name="departmentId" value={formData.departmentId} onChange={handleChange} className="w-full px-3 py-2 bg-white text-gray-900 border border-gray-300 rounded-lg focus:ring-2 focus:ring-primary outline-none">
                <option value={1}>Administration</option>
                <option value={2}>OPD</option>
                <option value={3}>Emergency</option>
                <option value={4}>Pediatrics</option>
                <option value={5}>Surgery</option>
                <option value={6}>Medical</option>
                <option value={7}>Laboratory</option>
                <option value={8}>Pharmacy</option>
                <option value={9}>Radiology</option>
                <option value={10}>Maternity</option>
                <option value={11}>ICU</option>
                <option value={12}>Cardiology</option>
              </select>
            </div>
           <div>
              <label className="block text-sm font-medium text-gray-700 mb-1">Role</label>
              <select name="roleId" value={formData.roleId} onChange={handleChange} className="w-full px-3 py-2 bg-white text-gray-900 border border-gray-300 rounded-lg focus:ring-2 focus:ring-primary outline-none">
                <option value={1}>Super Admin</option>
                <option value={2}>Hospital Admin</option>
                <option value={3}>Doctor</option>
                <option value={4}>Nurse</option>
                <option value={5}>Lab Technologist</option>
                <option value={6}>Pharmacist</option>
              </select>
            </div>
            <div className="md:col-span-2">
              <label className="block text-sm font-medium text-gray-700 mb-1">Qualifications (e.g., MBBS, FWACS, RN)</label>
              <input 
                name="qualification" 
                value={formData.qualification || ''} 
                onChange={handleChange} 
                className="w-full px-3 py-2 bg-white text-gray-900 border border-gray-300 rounded-lg focus:ring-2 focus:ring-primary outline-none" 
                placeholder="Enter professional qualifications"
              />
            </div>
            <div>
              <label className="block text-sm font-medium text-gray-700 mb-1">Rank / Designation (Auto-sets Salary)</label>
              <select name="rankId" value={formData.rankId} onChange={handleChange} className="w-full px-3 py-2 bg-white text-gray-900 border border-gray-300 rounded-lg focus:ring-2 focus:ring-primary outline-none">
                <option value={1}>Medical Officer (₦250,000)</option>
                <option value={2}>Senior Medical Officer (₦350,000)</option>
                <option value={3}>Nursing Officer (₦180,000)</option>
                <option value={4}>Senior Nursing Officer (₦220,000)</option>
                <option value={5}>Medical Lab Scientist (₦170,000)</option>
                <option value={6}>Pharmacist (₦200,000)</option>
              </select>
            </div>
            <div>
              <label className="block text-sm font-medium text-gray-700 mb-1">Phone Number</label>
              <input name="phone" value={formData.phone} onChange={handleChange} required className="w-full px-3 py-2 bg-white text-gray-900 border border-gray-300 rounded-lg focus:ring-2 focus:ring-primary outline-none" />
            </div>
          </div>

          {/* Modal Footer */}
          <div className="flex justify-end gap-3 pt-4 border-t border-gray-200">
            <button type="button" onClick={onClose} className="px-4 py-2 text-gray-700 bg-gray-100 rounded-lg hover:bg-gray-200 transition">Cancel</button>
            <button type="submit" disabled={loading} className="px-4 py-2 bg-primary text-white rounded-lg hover:bg-blue-700 transition flex items-center gap-2 disabled:opacity-50">
              {loading ? <Loader2 className="h-4 w-4 animate-spin" /> : (isEditing ? 'Update Staff' : 'Save Staff')}
            </button>
          </div>
        </form>
      </div>
    </div>
  );
}